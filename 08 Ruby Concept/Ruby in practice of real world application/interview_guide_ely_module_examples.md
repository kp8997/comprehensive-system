# 🧠 Senior Backend Engineer — Complete Interview Knowledge Guide

> Everything below is backed by **real code** from the E*** ** codebase.  
> Each section explains: **What it is → Why it matters → How the codebase does it → How to say it in an interview.**

---

## 1. 🏗️ The Service Object Pattern (Core Architecture)

### What is it?
Instead of stuffing all business logic into Models (fat model) or Controllers (fat controller), you extract complex operations into standalone **Service Objects** — plain Ruby classes with a single `call` method.

### How the codebase does it
The entire application is built on top of a custom [`ServiceBase`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/service_base.rb) class:

```ruby
class ServiceBase
  def initialize(*_args)
    @errors = []
  end

  class << self
    def call(*args, **kwargs)
      service_obj = new(*args, **kwargs)
      if service_obj.callable?
        service_obj.send(:before_call)
        call_result = service_obj.call
        service_obj.send(:after_call)
        call_result
      end
    end
  end

  def success?
    errors.blank?
  end

  def error?
    !success?
  end
end
```

**Key design decisions to highlight:**
- **`before_call` / `after_call` hooks:** These are lifecycle callbacks (like Rails controller filters). Any subclass can override them to add logging, authorization checks, or telemetry *without modifying the main logic*. This is the **Template Method Pattern**.
- **`callable?` guard:** Allows subclasses to conditionally skip execution (e.g., if a feature flag is off).
- **Error accumulation (not exceptions):** Instead of raising exceptions on every failure, errors are *collected* into an array. This lets a complex flow report multiple validation failures back to the user at once (e.g., "Email is invalid" AND "Start date is missing"), rather than stopping at the first one.

### How services compose together
Look at how [`TransferMembership`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/membership/transfer_membership.rb) orchestrates multiple sub-operations:

```ruby
def call
  return self unless valid?

  member.transaction do
    find_or_create_target_user
    generate_reset_password_token if success? && @new_user
    perform_transfer_membership   if success?
    populate_to_external_payroll  if success?
    migrate_user_to_idp           if success? && @new_user && !@target_user.idp_migrated?
    migrate_ebf_login_identity    if success?

    raise ActiveRecord::Rollback unless success?
  end
end
```

**Key pattern:** Each step is guarded by `if success?`. If any step fails, subsequent steps are skipped. If ANY step fails, `raise ActiveRecord::Rollback` rolls back the entire database transaction. This is the **Railway-Oriented Programming** pattern (Success track vs Failure track).

### 💬 Interview answer
> "I use Service Objects to encapsulate complex business operations. Each service has a single `call` method, accumulates errors instead of raising exceptions, and composes sub-services using a railway pattern — every step checks `if success?` before proceeding, and the entire operation is wrapped in a database transaction that rolls back if anything fails."

---

## 2. 🔒 Database Transactions & Data Integrity

### What is it?
A **transaction** guarantees that a group of database operations either ALL succeed or ALL fail together. This prevents your database from ending up in a corrupted half-finished state.

### How the codebase does it
In the [`Onboard`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/membership/creators/onboard.rb#L124-L146) service, creating a new employee involves saving to multiple tables (User, Member, SalaryVersion, etc.):

```ruby
def build_and_save_basic_info
  ActiveRecord::Base.transaction do
    build_member_params
    build_basic_info
    build_personal_detail
    raise ActiveRecord::Rollback if error?

    prepare_pay_details_audit_data
    skip_salary_validation
    track_user_personal_audit_data
    member.save!

    create_notice_period! if @notice_period_params.values.any?(&:present?)
    generate_personal_details_audit_trail
  end
rescue StandardError => e
  add_errors(e)
end
```

**What to notice:**
- `member.save!` uses the **bang version** (with `!`). This raises an exception if the save fails, which automatically triggers the transaction rollback.
- `raise ActiveRecord::Rollback if error?` — manual rollback if *our* validation logic (not the database's) detected a problem.
- The outer `rescue StandardError` ensures the service doesn't crash; it catches the error and adds it to the errors array so the caller gets a clean response.

### 💬 Interview answer
> "I wrap multi-table writes in `ActiveRecord::Base.transaction` blocks. I use bang methods like `save!` inside transactions so that database-level failures automatically trigger rollbacks. For application-level failures, I manually raise `ActiveRecord::Rollback`. I also wrap the entire transaction in a `rescue` to ensure failures are captured gracefully and returned to the caller — not swallowed or propagated as unhandled exceptions."

---

## 3. ⚡ Handling Race Conditions & Concurrency

### What is it?
A **race condition** occurs when two processes try to create or modify the same record at the same time. For example, two requests simultaneously trying to create a User with the same email.

### How the codebase does it
In [`TransferMembership`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/membership/transfer_membership.rb#L54-L70):

```ruby
def find_or_create_target_user
  @target_user = User.find_by(email: @target_user_email)
  @new_user = false
  if @target_user.nil?
    @target_user = initialize_user
    if @target_user.save
      @new_user = true
    else
      add_error(@target_user.errors.full_messages)
    end
  end
rescue ActiveRecord::RecordNotUnique => e
  @target_user = User.find_by(email: @target_user_email)
  @new_user = false
  Rails.logger.error(e)
end
```

**What's happening:**
1. Try to find the user.
2. If not found, try to create them.
3. If another thread created the same user between step 1 and step 2 (the race window), the **database unique index constraint** raises `ActiveRecord::RecordNotUnique`.
4. Instead of crashing, catch the exception and re-fetch the now-existing user.

This is the **"find or create with rescue"** pattern — a production-grade alternative to `find_or_create_by` which isn't atomic and can also race.

In [`BulkDeleteWorker`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/workers/todo_items/bulk_delete_worker.rb), a different race condition is handled:

```ruby
def perform(conditions_hash, retry_attempt = 0)
  deleted_count = TodoItems::BulkDeletable.destroy_dependent_todo_items(conditions_hash)

  # Handle race conditions (todo items are not created yet) by retrying
  if deleted_count.zero? && retry_attempt < RETRY_ATTEMPT
    retry_attempt += 1
    self.class.perform_in((retry_attempt * RETRY_DELAY).minutes, conditions_hash, retry_attempt)
  end
end
```

Here the race is: the delete worker runs *before* the creation worker has finished. So it finds nothing to delete. The solution: **exponential backoff retry** — re-enqueue with increasing delay.

### 💬 Interview answer
> "I handle race conditions at two levels. First, at the database level, I rely on **unique index constraints** and catch `RecordNotUnique` exceptions to gracefully recover. Second, at the application level for eventual consistency scenarios, I use **exponential backoff retries** — if a worker can't find the expected data yet, it re-enqueues itself with an increasing delay, capped at a maximum retry count."

---

## 4. 🔄 State Machines (AASM)

### What is it?
A **Finite State Machine** restricts an entity to a set of defined states and only allows specific transitions between them.

### How the codebase does it
The [`ScheduledTask`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/models/scheduled_task.rb#L195-L218) model:

```ruby
include AASM

aasm column: :status do
  state :pending, initial: true
  state :completed
  state :declined

  event :complete do
    after { touch :completed_at }
    transitions from: :pending, to: :completed
  end

  event :decline do
    transitions from: :pending, to: :declined, after: :update
  end

  event :pending do
    before do
      self.completed_at = nil
    end
    transitions to: :pending
  end
end
```

**Senior-level things to notice:**
- `column: :status` — maps the state machine to the `status` database column directly.
- `after { touch :completed_at }` — the moment a task moves to `completed`, the `completed_at` timestamp is automatically set using a **lifecycle callback**, not manual logic.
- `before { self.completed_at = nil }` — if a task is re-opened (back to pending), the completion timestamp is cleared.
- A task **cannot** go from `declined` to `completed`. The state machine enforces this at the code level. No need for manual `if/else` checks everywhere.

### 💬 Interview answer
> "I use AASM state machines to enforce valid entity lifecycles. For example, a Task can only transition from `pending` to `completed` — never from `declined` to `completed`. I also attach side-effects to transitions: when a task is completed, a callback automatically timestamps it; when it's re-opened, the timestamp is cleared. This keeps business rules centralized in the model rather than scattered across controllers and services."

---

## 5. 📨 Event-Driven Architecture (Kafka + Karafka)

### What is it?
Instead of Service A calling Service B directly (tight coupling), Service A publishes an **event** to a message broker (Kafka). Service B **consumes** events independently.

### How the codebase does it
The [`V2::TodoItemConsumer`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/consumers/v2/todo_item_consumer.rb) demonstrates production-grade Kafka consumption:

```ruby
class V2::TodoItemConsumer < ApplicationConsumer
  def consume
    payload_batch = []
    messages.each_with_index do |message, index|
      payload = message.payload.presence
      next unless payload

      payload_batch << payload

      if payload_batch.size >= bulk_handle_size
        bulk_handle(payload_batch)
        payload_batch = []
        mark_message(message)
      end
    rescue StandardError => e
      BugReporter.notify_index(e, { message: messages.payloads, payload_batch: payload_batch })
      mark_message(message)
      payload_batch = []  # Reset batch on error
    end
    bulk_handle(payload_batch) if payload_batch.present?
  end
end
```

**Senior-level concepts to highlight:**
1. **Batch processing:** Messages aren't processed one-by-one. They're accumulated into `payload_batch` and flushed when the batch reaches `bulk_handle_size` (configurable via ENV var). This dramatically reduces database write operations.
2. **Offset management:** `mark_message(message)` tells Kafka "I've processed up to here." If the consumer crashes, Kafka replays from the last marked offset. This guarantees **at-least-once delivery**.
3. **Error isolation:** If one message fails, the `rescue` block catches the error, reports it to the bug tracker, marks the offset (so Kafka doesn't replay the bad message), and **resets the batch** so the poison message doesn't corrupt the next batch.
4. **Observability:** `Datadog::Tracing.trace` wraps the batch handling to measure processing times and tag batch sizes per `item_type`.

On the **publishing** side, the codebase uses Kafka buses:
```ruby
# After processing a CSV file, publish an event
::Buses::FileAssetBus.pick_up({ event: EVENT_UPDATE_FILE_ASSET, uuid: file_id })
```

### 💬 Interview answer
> "I implement event-driven architectures using Kafka with batched consumers. Instead of processing each message individually, I accumulate payloads and flush them to the database in bulk once a batch threshold is hit. I handle poison messages by catching errors per-message, resetting the batch, and committing the offset so the bad message isn't replayed. I also wrap everything in APM traces for observability."

---

## 6. 🌐 gRPC for Inter-Service Communication

### What is it?
**gRPC** is a high-performance RPC framework that uses Protocol Buffers (protobuf) for serialization. It's faster than REST/JSON for internal service-to-service calls because it uses binary serialization and HTTP/2.

### How the codebase does it
The [`BaseHandler`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/grpc/rpc_handler/base_handler.rb) provides the foundation:

```ruby
module RpcHandler
  class BaseHandler
    extend EhProtobuf::Handler::Dsl
    eh_protobuf_service EhProtobuf::EmploymentHero
  end
end
```

A specific handler like [`TransferMembershipHandler`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/grpc/rpc_handler/transfer_membership_handler.rb):

```ruby
class TransferMembershipHandler < BaseHandler
  def call
    service = Membership::TransferMembership.new(
      member_uuid: request.member_id,
      target_user_email: request.target_user_email,
      ip_addresses: request.ip_addresses
    )
    service.call

    if service.success?
      return EhProtobuf::EmploymentHero::Membership::TransferMembershipResponse.new(
        user_id: service.target_user.uuid,
        reset_password_token: service.reset_password_token
      )
    end

    error = service.errors.first
    if error.is_a?(Membership::Errors::MemberNotFound)
      EhProtobuf::NotFoundError.new(error.to_s)
    else
      EhProtobuf::InvalidArgumentError.new(error.to_s)
    end
  end
end
```

**Key architecture insight:** The gRPC handler is a **thin adapter**. It:
1. Deserializes the protobuf `request` into plain Ruby arguments.
2. Delegates to the *same* `ServiceBase` service used by REST APIs.
3. Serializes the result back into a protobuf response.
4. Maps application errors to gRPC-standard error types (`NotFoundError`, `InvalidArgumentError`).

This means the core business logic is **transport-agnostic** — it works identically whether called via REST, gRPC, or a background worker.

### 💬 Interview answer
> "For internal service communication, I use gRPC with Protocol Buffers for type-safe, high-performance RPC calls. I keep gRPC handlers as thin adapters that delegate to the same service objects used by our REST APIs. This means our business logic is transport-agnostic. I also map our domain errors (like `MemberNotFound`) to standard gRPC error types for consistent error handling across services."

---

## 7. 🗄️ Caching Strategy (Redis + Cache Keys)

### What is it?
**Caching** stores frequently accessed data in fast storage (Redis) to avoid hitting the database on every request.

### How the codebase does it
[`CacheManagement::Services::Base`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/cache_management/services/base.rb) builds a generic, reusable caching framework:

```ruby
module CacheManagement::Services
  class Base < ::ServiceBase
    CACHE_EXPIRY_TIME = (ENV['CACHE_SERVICE_EXPIRE_TIME'] || 2).to_i.minutes

    def initialize(service_klass:, method_name:, attribute_name:, args:, kwargs: {}, options: {})
      @service_klass = service_klass
      @method_name = method_name
      @args = args
      @kwargs = kwargs
      @cache_expiry_time = (options[:cache_expiry_time] || CACHE_EXPIRY_TIME)
    end

    def postfix_digest
      @postfix_digest ||= OpenSSL::Digest::SHA256.hexdigest(@args.to_s + @kwargs.to_s)
    end

    def data_cache_key
      ActiveSupport::Cache.expand_cache_key(build_cache_key(:services, :data))
    end

    def invalidation_cache_key
      ActiveSupport::Cache.expand_cache_key(build_cache_key(:services, :invalidation))
    end
  end
end
```

**Senior-level concepts:**
- **SHA256 key digest:** Cache keys are built by hashing the method arguments (`@args.to_s + @kwargs.to_s`). This ensures that `get_member(id: 1)` and `get_member(id: 2)` have different cache keys, while identical calls always hit the same cache.
- **Dual cache keys (data + invalidation):** The `invalidation_cache_key` is a separate key that tracks whether the cached data is stale. This enables **cache invalidation** without deleting the data — you can mark data as stale and let the next read refresh it.
- **Environment-configurable TTL:** `ENV['CACHE_SERVICE_EXPIRE_TIME']` means operations teams can tune cache lifetimes without deploying code.
- **Validation:** The service validates that `service_klass` actually exists and has the specified `method_name`, preventing runtime NoMethodError surprises.

### 💬 Interview answer
> "I implement caching using a generic framework built on top of Redis. Cache keys are constructed using SHA256 digests of the method arguments, ensuring uniqueness. I use a dual-key strategy: one key for the data itself and a separate invalidation key to mark data as stale without deleting it. TTL values are environment-configurable so operations can tune them without code changes."

---

## 8. 🛡️ Authorization via Policy Objects

### What is it?
**Policy Objects** encapsulate authorization logic ("Can this user do this action?") into dedicated classes, keeping controllers and services clean.

### How the codebase does it
[`TodoItemPolicy`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/policies/todo_item_policy.rb):

```ruby
class TodoItemPolicy < ApplicationPolicy
  ENTITLEMENT_FLAG = :todo_items_entitlement

  def access?
    member_state_access?(allow_postboarding: true) && feature_flag_enabled?
  end

  def feature_flag_enabled?
    compute_permission(
      entitlement: ENTITLEMENT_FLAG,
      legacy_logic: -> { feature_flag_on?(:todo_items) },
    )
  end
end
```

**What makes this senior-level:**
- **Layered authorization:** `access?` checks TWO things: (1) Is the member in a valid state? (2) Is the feature flag enabled? Both must pass.
- **Entitlement vs Feature Flag migration:** `compute_permission` supports a newer `entitlement` system while falling back to a `legacy_logic` lambda for backward compatibility. This shows **safe, incremental migration** of authorization systems.
- **Memoized policies** in the API layer (from the onboard endpoint):
  ```ruby
  def employee_files_policy
    @employee_files_policy ||= EmployeeFilesPolicy.new(auth_context)
  end
  ```
  The `||=` memoization pattern ensures the Policy is only instantiated once per request, even if called multiple times.

### 💬 Interview answer
> "I separate authorization logic into dedicated Policy Objects. Each policy answers a single question like `access?`. Inside, I layer checks: member state validation, feature flags, and entitlement-based permissions. I also use memoization (`||=`) in controllers to avoid re-instantiating policies multiple times per request. When migrating between authorization systems, I use a `compute_permission` method that checks the new system first and falls back to legacy logic."

---

## 9. 🔧 Background Workers & Async Processing (Sidekiq)

### What is it?
**Sidekiq** processes background jobs asynchronously. Heavy operations (sending emails, processing CSV files, syncing with external payroll) are pushed to a Redis queue and processed by worker threads.

### How the codebase does it
[`BulkDeleteWorker`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/workers/todo_items/bulk_delete_worker.rb):

```ruby
class TodoItems::BulkDeleteWorker
  include Sidekiq::Worker

  RETRY_ATTEMPT = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_ATTEMPT'] || 3).to_i
  RETRY_DELAY = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_DELAY'] || 3).to_i

  sidekiq_options queue: :critical, retry: false

  def perform(conditions_hash, retry_attempt = 0)
    deleted_count = TodoItems::BulkDeletable.destroy_dependent_todo_items(
      conditions_hash.with_indifferent_access
    )

    if deleted_count.zero? && retry_attempt < RETRY_ATTEMPT
      retry_attempt += 1
      self.class.perform_in((retry_attempt * RETRY_DELAY).minutes, conditions_hash, retry_attempt)
    end
  end
end
```

**Senior-level concepts:**
- `queue: :critical` — This job is placed on a high-priority queue. Sidekiq processes queues in priority order, ensuring critical deletions aren't blocked by thousands of low-priority email jobs.
- `retry: false` — Disables Sidekiq's default retry mechanism because the worker implements **its own custom retry with exponential backoff** (`retry_attempt * RETRY_DELAY`). This gives finer control over retry timing and maximum attempts.
- `perform_in` — Schedules the *next* retry attempt to run in the future, not immediately.
- **Idempotency:** The `deleted_count.zero?` check makes the operation idempotent — if the items were already deleted (e.g., by a previous run), it simply stops retrying.

The CSV processing flow (in [`QuickAddFreemiumCsv`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/membership/creators/quick_add_freemium_csv.rb)) shows the full async pipeline:
1. **API receives CSV upload** → validates headers
2. **Pushes to Sidekiq worker** → worker processes rows in background
3. **Publishes Kafka event** → `::Buses::FileAssetBus.pick_up(...)` marks the file as used

### 💬 Interview answer
> "I use Sidekiq with named priority queues (`critical`, `default`, `low`) to ensure important jobs aren't starved. For complex retry scenarios, I disable Sidekiq's default retries and implement custom exponential backoff using `perform_in`, with configurable retry counts and delays via environment variables. I also ensure all workers are idempotent — running them twice produces the same result."

---

## 10. 📊 Observability & Tracing (Datadog APM)

### What is it?
**Observability** means being able to understand what your production system is doing at any moment. **Distributed tracing** tracks a single request as it flows across services.

### How the codebase does it
The [`Onboard`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/services/membership/creators/onboard.rb#L32-L102) service wraps each phase in a Datadog trace:

```ruby
def call
  Datadog::Tracing.trace 'onboard.basic_info' do
    build_and_save_basic_info
    return if error?
  end

  Datadog::Tracing.trace 'onboard.employment_and_payroll_detail' do
    create_employment_detail
    setup_positions
    create_payroll_detail
    build_pay_details
  end

  Datadog::Tracing.trace 'onboard.assign_benefits' do
    assign_benefits
    return if error?
  end

  Datadog::Tracing.trace 'onboard.third_party' do
    populate_to_external_payroll
    add_note_to_job_adder
    notify_employee_added_event
  end
end
```

And in the Kafka consumer, traces include **custom tags**:
```ruby
def bulk_handle(payload_batch)
  Datadog::Tracing.trace 'todo_item_consumer.bulk_handle' do |span|
    payload_batch.group_by { |p| p['item_type'] }.each do |item_type, payloads|
      span.set_tag("item_types.#{item_type}.count", payloads.size)
    end
    TodoItems::BulkHandler.new(payload_batch).handle
  end
end
```

**Why this matters:**
- If onboarding is slow, you can see in Datadog's flame graph exactly which phase (`basic_info`, `third_party`, etc.) is the bottleneck.
- The Kafka consumer tags batch sizes per type, so you can monitor if one item type is causing a spike.
- Combined with `BugReporter.notify_elysium(e)` for error tracking, this gives you **full visibility** into failures, latency, and throughput.

### 💬 Interview answer
> "I instrument critical business flows with Datadog APM traces, wrapping each phase of a complex operation in its own span. This creates a flame graph that shows exactly where time is spent. In Kafka consumers, I add custom tags for batch sizes and item types so we can monitor throughput per category. Combined with centralized error reporting, this gives us full visibility into performance bottlenecks and failure patterns in production."

---

## 🎯 Bonus: Advanced ActiveRecord Patterns Used in the Codebase

### Scopes & Query Objects
From [`ScheduledTask`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/models/scheduled_task.rb):
```ruby
scope :due_today, lambda {
  joins(:organisation)
    .where("scheduled_tasks.due_date = DATE(now() at time zone organisations.time_zone)")
}
```
This scope respects **per-organization timezone** — a critical detail for a global HR platform.

### Polymorphic Associations
From [`ScheduledTaskAssignee`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/models/scheduled_task.rb#L68-L107):
```ruby
has_many :scheduled_task_assign_teams, through: :scheduled_task_owners,
                                       source: :target,
                                       source_type: 'Team'

has_many :scheduled_task_assign_members, through: :scheduled_task_owners,
                                         source: :target,
                                         source_type: 'Member'
```
A single `scheduled_task_assignees` table can point to either a `Member` OR a `Team` using `source_type`. This avoids separate join tables for each assignee type.

### Enum with Integer Mapping
From [`TodoItem`](file:///Users/khangphan/Projects/employment%20hero/employment-hero/app/models/todo_item.rb#L118-L137):
```ruby
enum :item_type, {
  DOCUMENT_SENDING_SIGNATORY_SIGN => 2,
  CERTIFICATION_EMPLOYEE_UPDATE => 5,
  TASK => 18,
}
```
Explicit integer mapping (not auto-increment) means you can add new types without shifting existing values — critical for a production database.

### Composite Uniqueness Validation
```ruby
validates :original_item_id, uniqueness: { scope: %i[member_id item_type] }
```
This prevents duplicate todo items: a member can't have two "acknowledge policy X" items. The `scope` makes the uniqueness check a **compound key** across three columns.

---

## 📝 Summary Cheat Sheet

| Topic | Library / Pattern | Key Concept |
|-------|-------------------|-------------|
| Service Objects | Custom `ServiceBase` | Railway-oriented programming, error accumulation |
| Transactions | `ActiveRecord::Base.transaction` | Atomic writes, `Rollback` on failure |
| Race Conditions | `rescue RecordNotUnique` | Database-level uniqueness, retry with backoff |
| State Machines | `AASM` gem | Lifecycle callbacks, transition guards |
| Event Architecture | `Karafka` (Kafka) | Batch consuming, offset management, poison message handling |
| Inter-service RPC | `gRPC` + Protobuf | Transport-agnostic services, thin adapter handlers |
| Caching | Redis + `ActiveSupport::Cache` | SHA256 key digests, dual data/invalidation keys |
| Authorization | Policy Objects | Layered checks, entitlement migration, memoization |
| Background Jobs | `Sidekiq` | Priority queues, custom backoff, idempotency |
| Observability | `Datadog::Tracing` | Phase-level spans, custom tags, error reporting |
