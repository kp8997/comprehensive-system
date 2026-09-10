# 📖 Complete Backend Guide — From Config to Code

> Each technology below is covered in **three layers**:
> 1. 🧠 **Concept** — What is it, why, when (for beginners)
> 2. ⚙️ **Config** — How it's set up in production (real initializer code)
> 3. 💻 **Usage** — How it's used in application code (real service/model/worker code)
> 4. 💬 **Interview** — What to say

---

## Table of Contents
1. [gRPC & Protobuf](#1-grpc)
2. [Sidekiq — Background Jobs](#2-sidekiq)
3. [Kafka — Event-Driven Messaging](#3-kafka)
4. [Redis — Caching & Connection Pooling](#4-redis)
5. [Database Transactions & Race Conditions](#5-transactions)
6. [Encryption — Protecting Financial Data](#6-encryption)
7. [Observability — Datadog, Sentry, Logging](#7-observability)
8. [Feature Flags — Safe Rollouts](#8-feature-flags)
9. [Service Objects — The Architecture Pattern](#9-service-objects)
10. [Policy Objects — Authorization](#10-policies)
11. [State Machines — Entity Lifecycle](#11-state-machines)
12. [Docker — Containers](#12-docker)
13. [Async Polling — Long-Running Requests](#13-polling)
14. [Strong Migrations — Safe DB Changes](#14-migrations)

---

<a id="1-grpc"></a>
## 1. 🌐 gRPC & Protobuf

### 🧠 Concept
**REST API** is like sending a letter in English (JSON text). **gRPC** is like a phone call between robots speaking binary (Protobuf). It's faster and type-safe because both sides share a `.proto` file that defines the exact shape of requests and responses.

**EhProtobuf** is the company's Ruby gem wrapping Google's Protobuf. When you see `EhProtobuf::EmploymentHero::CheckMemberIsAtsRecruiterResponse`, it's a Ruby class auto-generated FROM a `.proto` file.

Use gRPC for internal service-to-service calls (fast, typed). Use REST for public APIs (browsers understand JSON).

---

### ⚙️ Config — `config/initializers/01_grpc.rb`

**Server middleware — code that wraps EVERY incoming gRPC call:**

```ruby
# MIDDLEWARE 1: Clean up database connections after every call
class GrpcActiveRecord < EhProtobuf::ServerMiddlewares::Base
  def request_response(request:, call:, method:)
    yield          # Run the handler
  ensure
    # ALWAYS release DB connections, even if the handler crashes.
    # Without this, the connection pool slowly leaks and the app freezes.
    ::ActiveRecord::Base.connection_handler.clear_active_connections!(
      ::ActiveRecord::Base.current_role
    )
    DatabaseReplica.cleanup_read_slaves_connections!
  end
end

# MIDDLEWARE 2: Report unexpected errors to Sentry
class GrpcErrorHandler < EhProtobuf::ServerMiddlewares::Base
  def request_response(request:, call:, method:)
    yield
  rescue StandardError => exception
    # Only report UNEXPECTED errors. Expected business errors (like "not found")
    # are EhProtobuf::BaseError — don't alert on those.
    unless exception.is_a?(EhProtobuf::BaseError)
      BugReporter.notify(exception, request: request.inspect, method: method.name)
    end
    raise
  end
end
```

**Server setup:**

```ruby
# Health check — verify database is alive before accepting traffic
active_record_examinee = proc do
  ActiveRecord::Base.connection.verify!
  true
rescue StandardError => e
  abort("Cannot start GRPC due to ActiveRecord error: #{e.message}")
end

EhProtobuf.config_server(
  EhProtobuf::EmploymentHero,
  host: ENV['EH_RPC_HOST'] || 'localhost',
  port: ENV['EH_RPC_PORT'] || 50_050,          # gRPC on port 50050
  pool_size: (ENV['EH_RPC_POOL_SIZE'] || 15).to_i,  # Max 15 concurrent calls
  logger: Rails.logger,
  server_middlewares: [          # ORDER MATTERS!
    GrpcActiveRecord,            # 1st (outermost): DB cleanup
    GrpcErrorHandler,            # 2nd: Error reporting
    ::AuditContextMiddleware,    # 3rd: Track who made the call
    Grpc::CurrentAttributesMiddleware  # 4th: Thread-local context
  ],
  health_examinees: [active_record_examinee]  # Load balancer checks this
)

# Health check on a SEPARATE port — load balancers ping this
EhProtobuf.config_healthcheck(host: ENV['EH_RPC_HOST'] || 'localhost', port: 40_000)
```

**Client connections to other microservices — each with tuned timeouts:**

```ruby
# Auth service — login must be fast
EhProtobuf.config_client(
  EhProtobuf::Auth,
  host: ENV['AUTH_RPC_HOST'] || 'localhost',
  port: ENV['AUTH_RPC_PORT'] || 50_051,
  timeout: (ENV['AUTH_RPC_TIMEOUT'] || 4).to_i   # 4 seconds max
)

# Virus scanner — intentionally slow
EhProtobuf.config_client(
  EhProtobuf::StorageVirusScan,
  host: ENV['STORAGE_VIRUS_SCAN_RPC_HOST'] || 'localhost',
  port: ENV['STORAGE_VIRUS_SCAN_RPC_PORT'] || 50_055,
  timeout: 60   # 60 seconds — scanning is CPU-heavy
)

# Feature flags — most complex config: retries + cache + load balancing
EhProtobuf.config_client(
  EhProtobuf::FeatureFlag,
  host: ENV['FEATURE_FLAG_RPC_HOST'] || 'localhost',
  port: ENV['FEATURE_FLAG_RPC_PORT'] || 50_053,
  timeout: (ENV['FEATURE_FLAG_RPC_TIMEOUT'] || 2).to_i,
  unavailable_retry_backoff: proc { |attempt| 2**attempt },  # 2s, 4s, 8s...
  caching: {
    distributed_cache_config: {
      error_callback: proc { |e| BugReporter.notify(e) },
      redis_pool_size: feature_flag_distributed_cache_redis_pool_size,
    }
  },
  client_load_balancing: feature_flag_client_lb_config
)

# ... 28 more services: ATS, Instapay, LMS, Files, Survey, etc.
```

**Why different timeouts?** Auth (4s) must be fast or users leave. Virus scanning (60s) is inherently slow. Feature flags (2s) are checked on every page load — if the flag service is slow, the entire app is slow.

---

### 💻 Usage — Real gRPC Handler Code

```ruby
# File: app/grpc/rpc_handler/base_handler.rb
# All gRPC handlers inherit from this

module RpcHandler
  class BaseHandler
    extend EhProtobuf::Handler::Dsl
    eh_protobuf_service EhProtobuf::EmploymentHero

    # Protobuf has no Date type — convert Ruby dates to strings
    def date_format(date)
      return '' if date.nil?
      date.strftime('%F')  # "2024-01-15"
    end
  end
end
```

```ruby
# File: app/grpc/rpc_handler/check_member_is_ats_recruiter_handler.rb
# Another service asks: "Is this member a recruiter?"

module RpcHandler
  class CheckMemberIsAtsRecruiterHandler < BaseHandler
    def initialize(request, meta)
      @request = request   # Protobuf message (auto-deserialized from binary)
      @meta = meta          # Metadata (auth tokens, trace IDs)
    end

    def call
      # request.member_id is GUARANTEED to be a string
      # because the .proto file defines it as `string member_id`
      @member = Member.find_by(uuid: request.member_id)

      if @member
        # Return a Protobuf response (binary, type-safe)
        EhProtobuf::EmploymentHero::CheckMemberIsAtsRecruiterResponse.new(
          is_ats_recruiter: ats_policy.recruiter?,
          is_recruiter_has_onboarding_permission: ats_policy.recruiter_has_onboarding_permission?
        )
      else
        # Return a Protobuf error (like HTTP 404 in gRPC)
        EhProtobuf::NotFoundError.new(
          I18n.t('sync_errors.member_with_uuid_not_found', member_uuid: request.member_id)
        )
      end
    end

    private

    def ats_policy
      @ats_policy ||= AtsPolicy.new(AuthContext.new(current_member: @member))
    end
  end
end
```

```ruby
# File: app/grpc/rpc_handler/transfer_membership_handler.rb
# KEY PATTERN: gRPC handler as THIN ADAPTER

module RpcHandler
  class TransferMembershipHandler < BaseHandler
    def call
      # Step 1: TRANSLATE Protobuf → Ruby arguments
      # Step 2: DELEGATE to the same ServiceBase service that REST uses
      service = Membership::TransferMembership.new(
        member_uuid: request.member_id,
        target_user_email: request.target_user_email,
        ip_addresses: request.ip_addresses
      )
      service.call

      # Step 3: TRANSLATE Ruby result → Protobuf response
      if service.success?
        EhProtobuf::EmploymentHero::Membership::TransferMembershipResponse.new(
          user_id: service.target_user.uuid,
          reset_password_token: service.reset_password_token
        )
      else
        error = service.errors.first
        BugReporter.notify_konohagakure(error, {
          member_uuid: request.member_id,
          target_user_email: request.target_user_email
        })
        # Map domain errors to gRPC standard errors
        if error.is_a?(Membership::Errors::MemberNotFound)
          EhProtobuf::NotFoundError.new(error.to_s)        # gRPC NOT_FOUND
        else
          EhProtobuf::InvalidArgumentError.new(error.to_s) # gRPC INVALID_ARGUMENT
        end
      end
    end
  end
end
```

**Why this is smart:** Business logic lives in ONE place (`Membership::TransferMembership`). The gRPC handler just translates between Protobuf and Ruby. REST endpoints use the same service. This is **Hexagonal Architecture** — core logic is transport-agnostic.

**How gRPC is used as a CLIENT (calling another service):**

```ruby
# File: app/services/membership/creators/onboard.rb (line 444)
# Calling the ATS service via gRPC:

response = EhProtobuf::Ats::Client.prepare_candidate_data(
  EhProtobuf::Ats::PrepareCandidateDataRequest.new(
    candidate_job_id: @candidate_job_id,
    member_id: member.uuid,
  )
)
BugReporter.notify_elysium(response.errors.first) unless response.success?
```

```ruby
# File: app/policies/ats_policy.rb (line 320)
# Cached gRPC call with Redis:

def have_access_permission?
  ActiveModel::Type::Boolean.new.cast(
    Rails.cache.fetch(ats_access_permission_cache_key, expires_in: 2.minutes) do
      response = EhProtobuf::Ats::Client.check_access_permission(
        member_id: current_member.uuid
      )
      response.success? ? response.result.accessible : false
    end
  )
end
```

### 💬 Interview answer
> "I configure gRPC servers with middleware chains for connection cleanup and error reporting, health checks for load balancers, and per-service client timeouts (1s for file lookups, 60s for virus scanning). Handlers are thin adapters that delegate to service objects shared with REST, making business logic transport-agnostic. I cache expensive gRPC permission checks in Redis with 2-minute TTLs."

---

<a id="2-sidekiq"></a>
## 2. ⚙️ Sidekiq — Background Job Processing

### 🧠 Concept
Your web server should respond in under 500ms. If an operation takes 30 seconds (bulk CSV import, sending 1000 emails), push it to a **background job queue**. Sidekiq uses Redis as the queue. A separate Sidekiq process picks up jobs and runs them.

---

### ⚙️ Config — `config/initializers/03_sidekiq.rb`

```ruby
# RELIABILITY: If Redis crashes during enqueue, jobs aren't lost
Sidekiq::Client.reliable_push! unless Rails.env.test?

# UNIQUE JOBS: Prevent duplicate processing (user double-clicks "Delete")
SidekiqUniqueJobs.configure do |config|
  config.enabled = !Rails.env.test?
  config.lock_ttl = (ENV['SUJ_LOCK_TTL'] || 4).to_i.hours  # Lock expires after 4 hours
end

# Redis connection with reconnection strategy
redis_config = {
  url: ENV['REDIS_URL'],
  timeout: 5,
  reconnect_attempts: ENV['SIDEKIQ_REDIS_RECONNECT_ATTEMPTS']
    &.split(',')&.map(&:to_i) || 3  # e.g. "1,2,4" → retry after 1s, 2s, 4s
}

Sidekiq.configure_server do |config|
  config.redis = redis_config

  # Datadog metrics for job monitoring
  if ENV['DD_AGENT_HOST'] && dd_enabled
    config.dogstatsd = -> { Datadog::Statsd.new(ENV['DD_AGENT_HOST'], 8125, namespace: 'sidekiq') }
  end

  config.error_handlers << Sidekiq::SidekiqErrorHandler.new   # Report errors to Sentry
  config.death_handlers << Sidekiq::SidekiqDeathHandler.new   # When job permanently fails
  config.super_fetch!  # Reliable fetch — prevents job loss during Sidekiq restart

  # === 14 SERVER MIDDLEWARES (order matters!) ===
  config.server_middleware do |chain|
    chain.prepend Sidekiq::ParameterFilter::FilterMiddleware  # FIRST: Redact passwords/tokens from logs
    chain.add Langsmith::Http::WorkerMiddleware::TracingServer # Distributed tracing (Datadog)
    chain.add Sidekiq::ThrottleMiddleware        # Rate-limit certain job types
    chain.add Sidekiq::Middleware::Server::Statsd # Send metrics to Datadog
    chain.add Sidekiq::TimeZoneMiddleware         # Set correct timezone per job
    chain.add Sidekiq::AuditTrailChannelServerMiddleware  # Restore "who triggered this"
    chain.add AuditHero::Plugins::Sidekiq::ContextMiddleware # Audit logging
    chain.add Sidekiq::RuntimeContextMiddleware   # Set runtime context
    chain.add Sidekiq::CurrentMemberContextMiddleware  # Set current member
    chain.add Sidekiq::SentryContextMiddleware     # Set Sentry scope
    chain.add FeatureFlagAssistant::Middleware::SidekiqCacheMiddleware  # Cache feature flags
    chain.add SidekiqUniqueJobs::Middleware::Server # Release unique lock after job
    chain.add Sidekiq::CurrentAttributesMiddleware  # Thread-local attributes
    chain.add Sidekiq::ParameterFilter::RestoreMiddleware  # LAST: Restore original params
  end
end
```

**Why `ParameterFilter` is FIRST and LAST:** First, it redacts sensitive data (passwords, tokens) so they don't appear in traces/logs. Last, it restores the original data so the actual job can read it. This is **security by design**.

---

### 💻 Usage — Real Worker Code

```ruby
# File: app/workers/todo_items/bulk_delete_worker.rb

class TodoItems::BulkDeleteWorker
  include Sidekiq::Worker

  # ENV-configurable retry settings (ops can tune without code deploy)
  RETRY_ATTEMPT = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_ATTEMPT'] || 3).to_i
  RETRY_DELAY   = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_DELAY'] || 3).to_i

  # queue: :critical → processed before :default or :low jobs
  # retry: false → we implement our OWN retry logic below
  sidekiq_options queue: :critical, retry: false

  def perform(conditions_hash, retry_attempt = 0)
    deleted_count = TodoItems::BulkDeletable.destroy_dependent_todo_items(
      conditions_hash.with_indifferent_access
    )

    # RACE CONDITION: Items might not be created yet. Retry with backoff.
    # 1st retry: 3 min, 2nd: 6 min, 3rd: 9 min
    if deleted_count.zero? && retry_attempt < RETRY_ATTEMPT
      retry_attempt += 1
      self.class.perform_in((retry_attempt * RETRY_DELAY).minutes, conditions_hash, retry_attempt)
    end
  end
end
```

**How it's called from the API:**

```ruby
# In the employee_onboard.rb Grape API endpoint:
OnboardPollingWorker.perform_async(
  current_membership&.id,
  current_user.id,
  declared(params, include_missing: false).as_json,
  redis_key
)
# .perform_async → pushes to Redis queue, returns in <1ms
# The actual onboarding runs in the background
```

### 💬 Interview answer
> "I configure Sidekiq with `reliable_push!` and `super_fetch!` for guaranteed delivery, 14 server middlewares handling parameter filtering, tracing, throttling, and audit context. I use priority queues (`:critical` > `:default`) and custom exponential backoff with ENV-configurable retry params."

---

<a id="3-kafka"></a>
## 3. 📨 Kafka — Event-Driven Messaging

### 🧠 Concept
Instead of Service A calling Service B directly, A publishes an event to Kafka (a shared message board). B reads it when ready. Multiple services can read the same event independently.

---

### ⚙️ Config — `config/initializers/double_decker_bus.rb`

```ruby
DoubleDeckerBus.configure do |config|
  # Kafka broker connection (with TLS in production)
  config.kafka_client = if ENV['KAFKA_BROKERS_TLS']
    { seed_brokers: ENV['KAFKA_BROKERS_TLS'].split(','), ssl_ca_certs_from_system: true }
  else
    { seed_brokers: (ENV['KAFKA_BROKERS'] || 'kafka://127.0.0.1:9092').split(',') }
  end.merge({ client_id: 'employment-hero' })

  # ASYNC delivery — don't block web requests
  config.delivery_settings = {
    async: true,
    async_threshold: 1,       # Flush after 1 message or...
    async_interval: 1,        # ...every 1 second or...
    async_idle_threshold: 1,  # ...1 second of no new messages
    graceful_timeout: 15,     # Wait 15s for in-flight messages during shutdown
  }

  config.error_handler = proc { |exception| Sentry.capture_exception(exception) }
  config.graveyard_settings = redis_ddb_config  # Store failed messages for retry

  # === AUTO-PUBLISH EVENTS FROM ACTIVERECORD MODELS ===
  config.hook_settings = {
    adapter: :active_record,
    hooks: [
      {
        bus: 'basic_info',
        events: %i[create update],
        callback: :after_commit,   # ONLY after transaction commits!
        models: [
          Organisation, Member, User, LeaveRequest, Team, TeamMember,
          BankAccount, Bank, PayrollInfo, SalaryVersion,
          # ... 30+ models
        ],
        async: true
      },
      {
        bus: 'destroy_basic_info',
        events: %i[destroy],
        callback: :after_commit,
        models: [ Organisation, Member, User, BankAccount, Bank, ... ],
      },
    ]
  }
end
```

**`callback: :after_commit`** is CRITICAL. If you publish during a transaction that later rolls back, other services process events for data that doesn't exist. `after_commit` guarantees the data is actually saved.

---

### 💻 Usage — Real Kafka Consumer

```ruby
# File: app/consumers/v2/todo_item_consumer.rb

class V2::TodoItemConsumer < ApplicationConsumer
  def consume
    payload_batch = []

    messages.each_with_index do |message, index|
      payload = message.payload.presence
      next unless payload    # Skip empty messages

      payload_batch << payload

      # BATCH FLUSH: When batch reaches configured size (default 10)
      if payload_batch.size >= bulk_handle_size
        bulk_handle(payload_batch)  # Write all 10 to DB at once
        payload_batch = []
        mark_message(message)       # Tell Kafka "processed up to here"
      end

    # POISON MESSAGE HANDLING: One bad message doesn't block the entire consumer
    rescue StandardError => e
      BugReporter.notify_index(e, { message: messages.payloads, payload_batch: payload_batch })
      mark_message(message)    # Commit offset (don't replay the bad message)
      payload_batch = []       # Reset batch
    end

    bulk_handle(payload_batch) if payload_batch.present?  # Don't forget leftovers!
  rescue StandardError => e
    BugReporter.notify_index(e, { message: messages.payloads })
  end

  private

  def bulk_handle(payload_batch)
    Datadog::Tracing.trace 'todo_item_consumer.bulk_handle' do |span|
      # Tag the Datadog span with item type counts for monitoring
      payload_batch.group_by { |p| p['item_type'] }.each do |item_type, payloads|
        span.set_tag("item_types.#{item_type}.count", payloads.size)
      end
      TodoItems::BulkHandler.new(payload_batch).handle
    end
  end

  def bulk_handle_size
    (ENV['TODO_ITEM_CONSUMER_BULK_HANDLE_SIZE'] || 10).to_i  # Tunable via ENV
  end
end
```

**Publishing side** — auto-triggered by hooks, OR explicit:
```ruby
# After CSV processing, notify file service:
::Buses::FileAssetBus.pick_up({ event: 'update', uuid: file_id })
```

### 💬 Interview answer
> "I configure Kafka with async delivery and `after_commit` hooks to auto-publish events from 30+ ActiveRecord models. Consumers batch messages (configurable via ENV), handle poison messages by catching errors and committing offsets, and instrument each batch with Datadog trace tags."

---

<a id="4-redis"></a>
## 4. 🗄️ Redis — Caching & Connection Pooling

### 🧠 Concept
Redis stores data in memory (RAM), ~100x faster than a database. Used for: caching, Sidekiq job queue, distributed locks, feature flag cache.

---

### ⚙️ Config — `config/initializers/redis.rb` + `redis_mutex.rb`

```ruby
# CACHE REDIS — with connection pooling
CACHE_REDIS_POOL = ConnectionPool.new(
  timeout: 5,                                              # Wait max 5s for a connection
  size: (ENV['REDIS_API_POOL'] || 5).to_i                  # 5 connections in the pool
) do
  Redis.new(
    url: ENV['REDIS_CACHE_URL'] || ENV['REDIS_URL'],
    reconnect_delay: (ENV['REDIS_API_DELAY'] || 1).to_f,       # 1s before reconnecting
    reconnect_delay_max: (ENV['REDIS_API_DELAY_MAX'] || 10).to_f, # Max 10s wait
    reconnect_attempts: (ENV['REDIS_API_RECONNECT_ATTEMPTS'] || 3).to_i,
    namespace: ENV['REDIS_NAMESPACE']  # Prefix all keys to avoid collisions
  )
end

# MUTEX REDIS — SEPARATE instance for distributed locks
# Why separate? Cache storms shouldn't block lock acquisition
RedisClassy.redis = Redis.new(
  url: (ENV['REDIS_MUTEX_URL'] || ENV['REDIS_URL']),
  namespace: ENV['REDIS_NAMESPACE']
)
```

---

### 💻 Usage — Real Caching Code

**Generic cache framework:**
```ruby
# File: app/services/cache_management/services/base.rb

module CacheManagement::Services
  class Base < ::ServiceBase
    CACHE_EXPIRY_TIME = (ENV['CACHE_SERVICE_EXPIRE_TIME'] || 2).to_i.minutes

    # SHA256 hash of arguments → unique cache key per call
    def postfix_digest
      @postfix_digest ||= OpenSSL::Digest::SHA256.hexdigest(@args.to_s + @kwargs.to_s)
    end

    # Cache key: "services/data/UserService/find_by_email/a1b2c3d4..."
    def data_cache_key
      ActiveSupport::Cache.expand_cache_key(build_cache_key(:services, :data))
    end

    # SEPARATE invalidation key — mark data stale without deleting
    def invalidation_cache_key
      ActiveSupport::Cache.expand_cache_key(build_cache_key(:services, :invalidation))
    end
  end
end
```

**Cache in a Policy (real production pattern — 3 layers of optimization):**
```ruby
# File: app/policies/ats_policy.rb

def hiring_manager_role?
  # LAYER 1: Instance memoization — don't recompute within same request
  return @_hiring_manager_role if defined?(@_hiring_manager_role)

  @_hiring_manager_role = ActiveModel::Type::Boolean.new.cast(
    # LAYER 2: Redis cache — don't make gRPC calls for 2 minutes
    Rails.cache.fetch(ats_hiring_manager_cache_key, expires_in: HIRING_MANAGER_CACHE_PERIOD) do
      # LAYER 3: This block ONLY runs on cache miss — makes actual gRPC call
      response = EhProtobuf::Ats::Client.check_hiring_manager(member_id: current_member.uuid)
      if response.success?
        response.result.accessible
      else
        BugReporter.notify(response.first_error, {}, issue_owner: '#squad-jokers')
        false  # SAFE DEFAULT: if call fails, deny access
      end
    end
  )
end
```

### 💬 Interview answer
> "I configure Redis with connection pooling sized to match thread count, progressive reconnection delays, and namespace isolation. I use separate Redis instances for caching and distributed locks. For expensive permission checks, I use three optimization layers: instance memoization, Redis cache with TTL, and safe defaults on failure."

---

<a id="5-transactions"></a>
## 5. 🔒 Database Transactions & Race Conditions

### 🧠 Concept
A **transaction** groups database operations into all-or-nothing. If step 3 of 5 fails, steps 1-2 are undone.

A **race condition** is when two requests happen simultaneously and interfere with each other (both try to create the same user).

---

### 💻 Usage — Real TransferMembership Service

```ruby
# File: app/services/membership/transfer_membership.rb

def call
  return self unless valid?

  @user = member.user
  @old_user_uuid = @user.uuid

  # === TRANSACTION: Everything inside is all-or-nothing ===
  member.transaction do
    find_or_create_target_user                                        # Step 1
    generate_reset_password_token if success? && @new_user            # Step 2
    perform_transfer_membership if success?                           # Step 3
    populate_to_external_payroll if success?                          # Step 4
    migrate_user_to_idp if success? && @new_user && !@target_user.idp_migrated?
    migrate_ebf_login_identity if success?                            # Step 6

    # SAFETY NET: If ANY step failed, undo ALL database changes
    raise ActiveRecord::Rollback unless success?
  end
  self
end

# === RACE CONDITION HANDLING ===
def find_or_create_target_user
  @target_user = User.find_by(email: @target_user_email)
  @new_user = false

  if @target_user.nil?
    @target_user = User.new(email: @target_user_email, password: SecureRandom.uuid)
    if @target_user.save
      @new_user = true
    else
      add_error(@target_user.errors.full_messages)
    end
  end

# Two admins click Transfer at the same instant for the same email.
# Both check find_by → both get nil → both try to create → one hits unique constraint.
rescue ActiveRecord::RecordNotUnique => e
  @target_user = User.find_by(email: @target_user_email)  # Just use the one the OTHER process created
  @new_user = false
  Rails.logger.error(e)
end

# === OUTBOX PATTERN ===
def migrate_ebf_login_identity
  if success?
    # Write to outbox table INSIDE the transaction.
    # A separate worker picks this up AFTER transaction commits.
    # Guarantees: message is sent IF AND ONLY IF the transaction commits.
    EbfUserMigration.create!(
      original_user_uuid: @old_user_uuid,
      target_user_uuid: @target_user.uuid,
      target_email: @target_user_email,
      flow: EbfUserMigration::FLOWS[:transfer_membership]
    )
  end
end

# === FINANCIAL TRANSFER ===
def transfer_hero_balance
  service = TransferHeroBalance.new(@member_uuid, target_user, ip_addresses)
  service.call
  add_error(service.errors) unless service.success?
  # If this fails, the transaction rolls back — no partial money movement
end
```

### 💬 Interview answer
> "I use database transactions with Railway pattern (`if success?` chains) for multi-step operations. Race conditions on unique constraints are handled with `rescue RecordNotUnique` and re-fetch. I use the Transactional Outbox pattern — writing to an outbox table inside the transaction guarantees exactly-once delivery to downstream services."

---

<a id="6-encryption"></a>
## 6. 🔐 Encryption — Protecting Financial Data

### ⚙️ Config — `config/initializers/encryptor.rb`

```ruby
Encryptor.default_options.merge!({
  algorithm: AttrEncryptable::ALGORITHM,  # AES-256-CBC
  key: ENV['ENCRYPTOR_KEY'],              # From environment variable — NEVER in code
  iv: ENV['ENCRYPTOR_IV'],                # Initialization Vector
  salt: ENV['ENCRYPTOR_SALT'],
  mode: :per_attribute_iv_and_salt        # Each field gets its OWN IV and salt
})
```

**`per_attribute_iv_and_salt`**: Even if two employees have the same account number, their encrypted values are completely different. This prevents pattern analysis.

---

### 💻 Usage — BankAccount Model

```ruby
# File: app/models/bank_account.rb

class BankAccount < ApplicationRecord
  include AuditHero::DSL::Auditable        # Log every change for compliance
  include AuditTrail::Auditable
  include AttrEncryptable

  # These lines encrypt the data AUTOMATICALLY before saving to DB
  # DB stores: encrypted_account_number (gibberish)
  # Ruby reads: account_number (decrypted automatically)
  attr_encrypted :account_number, key: ENV['ENCRYPTION_KEY']
  attr_encrypted :bsb, key: ENV['ENCRYPTION_KEY']

  # Country-specific validation (Strategy Pattern)
  validates_with BankAccountValidator::Au, if: proc { member.work_location_au? }
  validates_with BankAccountValidator::Uk, if: proc { member.work_location_uk? }
  validates_with BankAccountValidator::Sg, if: proc { member.work_location_sg? }

  # SQL injection protection
  validates :account_name, injection: true
  validates :account_number, injection: true

  # SALTED HASH for change detection without decryption
  def update_bsb_account_number_hash
    self.bsb_account_number_hash_salt = SecureRandom.hex
    self.bsb_account_number_hash = Digest::SHA256.hexdigest(
      "#{hash_bsb_account_number_input}#{bsb_account_number_hash_salt}"
    )
  end

  # AUDIT: Before deleting, save a copy for reconciliation
  before_destroy :cache_deleted_attributes
  def cache_deleted_attributes
    DeletedBankAccount.create(account_number: account_number, bsb: bsb, member_id: member_id)
  end
end
```

### 💬 Interview answer
> "I encrypt financial data at the column level using AES-256-CBC with per-attribute IV and salt. Keys come from environment variables. I use salted SHA256 hashes for change detection without decrypting. Before deletion, a copy is cached for financial reconciliation. All changes are audited via AuditHero."

---

<a id="7-observability"></a>
## 7. 📊 Observability — Datadog, Sentry, Logging

### ⚙️ Config — Three layers working together

**Datadog** (`config/initializers/datadog.rb`):
```ruby
Datadog.configure do |c|
  # Embed trace IDs directly in SQL queries!
  c.tracing.instrument :pg, comment_propagation: 'full'
  # Result: SELECT * FROM members /* dd.trace_id='abc123' */
end
STATSD_CLIENT = Datadog::Statsd.new(ENV['DD_AGENT_HOST'], 8125)
```

**Sentry** (`config/initializers/04_bug_reporter.rb`):
```ruby
SAMPLING_ERROR_RATES = {
  EhProtobuf::UnavailableError     => { limit_by: :sample_rate, rate: 0.2 },       # 20%
  EhProtobuf::DeadlineExceededError => { limit_by: :time_period, period: 60.seconds, rate_limit: 2 },
  Users::Errors::NotHaveMembershipsError => { limit_by: :sample_rate, rate: 0.0 },  # Never report
}

Sentry.init do |config|
  config.send_default_pii = false         # NEVER send user data (PCI/GDPR)
  config.traces_sample_rate = 0           # Tracing via Datadog, not Sentry
  config.before_send = Sentry::SampleCapturingError  # Apply sampling rules
end
```

**Lograge** — structured logs with Datadog trace IDs (`config/initializers/lograge.rb`):
```ruby
config.lograge.custom_options = lambda do |event|
  correlation = ::Datadog::Tracing.correlation
  { dd: { trace_id: correlation&.trace_id&.to_s, span_id: correlation&.span_id&.to_s } }
end
```

---

### 💻 Usage — Phased Tracing in Onboard Service

```ruby
# File: app/services/membership/creators/onboard.rb

def call
  Datadog::Tracing.trace 'onboard.basic_info' do
    build_and_save_basic_info
    return if error?           # FAIL FAST — don't continue if core data fails
  end

  Datadog::Tracing.trace 'onboard.employment_and_payroll_detail' do
    create_employment_detail
    setup_positions
    create_payroll_detail
  end

  Datadog::Tracing.trace 'onboard.third_party' do
    populate_to_external_payroll    # Most failure-prone phase
    add_note_to_job_adder
    notify_employee_added_event
  end

rescue StandardError => e
  issue_sentry_report(e, '#squad-index')  # Alert the squad's Sentry channel
  errors << e.message
end
```

**Datadog flame graph result:**
```
onboard.basic_info              ████  (200ms)
onboard.employment_detail       ██    (100ms)
onboard.third_party             █████████████████████████ (9700ms) ← BOTTLENECK!
```

### 💬 Interview answer
> "I combine three observability layers: Datadog APM with SQL comment propagation for trace-to-query correlation, Sentry with error sampling (20% for noisy errors, rate-limited for bursts), and Lograge with embedded trace IDs. Critical flows are instrumented with phased Datadog spans so I can pinpoint exactly which phase is the bottleneck."

---

<a id="8-feature-flags"></a>
## 8. 🚩 Feature Flags

### ⚙️ Config — `config/initializers/feature_flag_assistant.rb`

```ruby
# Production: in-memory cache, sync every 3 minutes
FeatureFlagAssistant.working_mode = :in_memory
FeatureFlagAssistant.update_interval = 180  # seconds

# Context resolvers — extract org/member info for targeted flags
FeatureFlagAssistant.register_context_resolver(:organisation) do |org|
  { country: org.country, plan_name: org.current_subscription_plan&.name }
end

# gRPC retries: 0.5s, 1s, 2s
FeatureFlagAssistant.grpc_retry_options = {
  retriable_errors: [EhProtobuf::DeadlineExceededError, EhProtobuf::ResourceExhaustedError],
  retry_sleep_time_fn: ->(retry_count) { 2 ** (retry_count - 2) },
  retry_attempts: 3
}
```

### 💻 Usage — In Policy Objects

```ruby
# File: app/policies/todo_item_policy.rb

class TodoItemPolicy < ApplicationPolicy
  def access?
    member_state_access?(allow_postboarding: true) && feature_flag_enabled?
  end

  def feature_flag_enabled?
    # Check new entitlement system FIRST, fall back to legacy feature flag
    compute_permission(
      entitlement: :todo_items_entitlement,
      legacy_logic: -> { feature_flag_on?(:todo_items) },
    )
  end
end
```

### 💬 Interview answer
> "Feature flags use in-memory caching with 3-minute sync for zero-latency checks. Context resolvers automatically extract organisation attributes for targeted rollouts. When migrating between systems, `compute_permission` checks the new entitlement first and falls back to legacy flags."

---

<a id="9-service-objects"></a>
## 9. 🏗️ Service Objects

### 💻 The Foundation — ServiceBase

```ruby
# File: app/services/service_base.rb

class ServiceBase
  def initialize(*_args)
    @errors = []  # Every service starts with empty errors
  end

  class << self
    def call(*args, **kwargs)
      service_obj = new(*args, **kwargs)
      if service_obj.callable?        # Feature flag gate
        service_obj.send(:before_call) # Pre-flight hook
        call_result = service_obj.call # Main logic
        service_obj.send(:after_call)  # Post-flight hook
        call_result
      end
    end
  end

  def success?
    errors.blank?
  end

  def callable?
    true  # Override to check feature flags
  end

  def before_call; end   # Override for logging/telemetry
  def after_call; end

  private

  def add_error(error)
    @errors ||= []
    if error.is_a?(Array)
      error.each { |err| add_error(err) }
    else
      @errors << (error.is_a?(StandardError) ? error : StandardError.new(error))
    end
  end
end
```

### 💻 Real Service Using It

```ruby
# File: app/services/membership/creators/quick_add_freemium_csv.rb

module Membership::Creators
  class QuickAddFreemiumCsv < ServiceBase
    CSV_HEADERS_MAPPING_COLUMNS = {
      'First name [*]': 'first_name',
      'Last name [*]': 'last_name',
      'Account email [*]': 'email',
    }.freeze  # .freeze = immutable, can't be modified at runtime

    def call
      fetch_and_validate_csv_content   # Step 1: Download + validate
      return self if error?            # RAILWAY PATTERN: stop on failure

      create_new_members               # Step 2: Create records
      return self if error?

      mark_file_as_used                # Step 3: Publish Kafka event
      self
    end

    private

    def fetch_csv_content
      service = ::Files::GetFile.call(file_id, current_membership.uuid,
                                      current_membership.organisation.uuid)
      add_errors(service.errors) unless service.success?
      return unless service.success?

      # 5 validation layers:
      # 1. File service response check
      # 2. File exists check
      # 3. File type check (.csv, not .exe)
      # 4. CSV parseable check
      # 5. Header match check
      @csv_content = CSV.parse(URI.parse(file_url).read, headers: true)
    rescue OpenURI::HTTPError, CSV::MalformedCSVError => e
      add_error "Cannot open: #{e.message}"
    rescue StandardError => e
      add_error(I18n.t('errors.messages.generic_error'))
      BugReporter.notify_elysium(e)
    end
  end
end
```

---

<a id="13-polling"></a>
## 13. 🔄 Async Polling — Long-Running API Requests

### 💻 Real API Endpoint + Worker Pattern

```ruby
# File: app/api/employment_hero/v3/organisations/members/employee_onboard.rb

def process_onboard
  authorize_onboarding!

  # Step 1: Create Redis tracking key
  redis_key = "polling_onboard_#{current_organisation.id}_#{Time.current.to_i}"
  service = RedisPollingServiceV2.new(redis_key)
  service.start!  # Set initial state "processing" in Redis

  # Step 2: Push to background worker (returns instantly)
  OnboardPollingWorker.perform_async(
    current_membership&.id,
    current_user.id,
    declared(params, include_missing: false).as_json,
    redis_key
  )

  # Step 3: Return polling key to frontend in <50ms
  GoogleJsonResponse.render({ polling_key: service.redis_key })
end

# === POLLING STATUS ENDPOINT ===
# Frontend calls this every 2-3 seconds
get 'onboard/polling_status' do
  redis_polling_service = RedisPollingServiceV2.new(params[:polling_key])
  GoogleJsonResponse.render(
    status: redis_polling_service.state,     # "processing" | "completed" | "failed"
    errors: redis_polling_service.content&.dig(:errors),
    redirect_url: redis_polling_service.content&.dig(:data, :redirect_url),
  )
end
```

**Flow diagram:**
```
Frontend                  API                  Sidekiq Worker       Redis
  |--POST /onboard ------->|                       |                 |
  |                        |--Create key---------->|                 |
  |                        |--Enqueue worker------>|                 |
  |<--{ polling_key }-----|                       |--Start work---->|
  |--GET /polling_status ->|<-"processing"--------|                 |
  |--GET /polling_status ->|<-"processing"--------|--Update-------->|
  |--GET /polling_status ->|<-"completed"---------|-                |
  |    redirect to profile |                       |                 |
```

### 💬 Interview answer
> "For operations exceeding 100ms, I use async polling: the API returns a Redis-backed polling key instantly, a Sidekiq worker processes in background and updates the Redis key, and the frontend polls a status endpoint until completion."

---

<a id="14-migrations"></a>
## 14. 🛡️ Strong Migrations — Safe Database Changes

### ⚙️ Config — `config/initializers/strong_migrations.rb`

```ruby
StrongMigrations.start_after = 20_211_005_070_910  # Only check new migrations
StrongMigrations.auto_analyze = true                # Update query planner after index changes
StrongMigrations.target_version = 13                # Match production PostgreSQL version

# These operations MUST use lock timeouts to avoid locking tables for minutes
MIGRATIONS_WITH_LOCK_TIMEOUT = %i(
  add_column drop_table remove_column
  add_foreign_key change_column_null
).freeze
```

**Why?** `ALTER TABLE users ADD COLUMN phone VARCHAR` on a 10-million-row table **locks the entire table**. During the lock, every query to `users` waits. The app goes down. Strong Migrations blocks this and suggests the safe alternative.

---

## 📋 Master Cheat Sheet

| Technology | Config File | Key Settings | Real Usage File | Key Pattern |
|-----------|------------|-------------|----------------|-------------|
| gRPC | `01_grpc.rb` | 4 middlewares, 30+ clients, per-service timeouts | `transfer_membership_handler.rb` | Thin adapter + Service delegation |
| Sidekiq | `03_sidekiq.rb` | `reliable_push!`, 14 middlewares, priority queues | `bulk_delete_worker.rb` | Custom exponential backoff |
| Kafka | `double_decker_bus.rb` | `after_commit` hooks, async delivery, Redis graveyard | `todo_item_consumer.rb` | Batch processing + poison message isolation |
| Redis | `redis.rb` | Connection pooling, progressive reconnection | `cache_management/base.rb` | SHA256 keys, dual-key invalidation |
| Sentry | `04_bug_reporter.rb` | Error sampling (20%, 2/min cap), PII disabled | `onboard.rb` | `issue_sentry_report(e, '#squad-index')` |
| Datadog | `datadog.rb` | SQL comment propagation, StatsD | `onboard.rb` | Phased `Datadog::Tracing.trace` spans |
| Encryption | `encryptor.rb` | AES-256-CBC, per-attribute IV/salt | `bank_account.rb` | `attr_encrypted`, salted hashing |
| Feature Flags | `feature_flag_assistant.rb` | In-memory cache, 3-min sync | `todo_item_policy.rb` | `compute_permission` with fallback |
| Transactions | N/A (Rails built-in) | N/A | `transfer_membership.rb` | Railway pattern + `ActiveRecord::Rollback` |
| State Machine | N/A (AASM gem) | N/A | `scheduled_task.rb` | `aasm column: :status` |
| Strong Migrations | `strong_migrations.rb` | Lock timeout, auto-analyze | Migration files | Block dangerous DDL |
