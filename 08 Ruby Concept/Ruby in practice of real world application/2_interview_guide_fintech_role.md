# 🎯 Senior Backend Engineer — Fintech / Payment Role Interview Guide

> Adapted from real production codebase experience.
> Structured to directly match the **Nice to Have** and **What We Value** sections of the job description.

---

## Part A: Nice to Have — Technical Skills

---

### 1. 💰 Payment & Fintech Experience

#### What the JD wants
> Payment or fintech experience.

#### What you've actually done
You worked on an HR/Payroll SaaS platform that processes **financial data at scale** — bank accounts, payroll syncing, salary management, and an instant-pay (early wage access) feature called InstaPay.

#### Key fintech patterns you used

**a) Sensitive Financial Data Handling (PII/PCI-adjacent)**
Bank account models encrypt sensitive fields at the column level using AES-256:

```ruby
class BankAccount < ApplicationRecord
  include AttrEncryptable

  attr_encrypted :account_number, key: ENV['ENCRYPTION_KEY']
  attr_encrypted :bsb, key: ENV['ENCRYPTION_KEY']
end
```

This pattern extends across **tax file numbers, IRD numbers, national insurance numbers, OAuth tokens** — any data that, if breached, could cause financial harm. The encryption key is stored in environment variables, never in the codebase.

**b) Country-Specific Financial Validation**
Financial rules differ by country. Rather than one massive validator, the codebase uses **strategy-pattern validators**:

```ruby
validates_with BankAccountValidator::Au, if: proc { member.work_location_au? }
validates_with BankAccountValidator::Uk, if: proc { member.work_location_uk? }
validates_with BankAccountValidator::Sg, if: proc { member.work_location_sg? }
validates_with BankAccountValidator::My, if: proc { member.work_location_my? && member.my_localisation? }
```

Each validator class encapsulates the specific rules for that country's banking system (BSB format for AU, sort codes for UK, etc.).

**c) Transactional Integrity for Financial Operations**
When transferring a membership (which involves moving bank accounts and payroll data between users), the entire operation is wrapped in a database transaction with explicit rollback:

```ruby
def call
  member.transaction do
    find_or_create_target_user
    perform_transfer_membership   if success?
    populate_to_external_payroll  if success?
    migrate_ebf_login_identity    if success?

    raise ActiveRecord::Rollback unless success?
  end
end
```

If any step fails — user creation, payroll sync, identity migration — **everything rolls back**. No partial financial state is ever persisted.

#### 💬 How to say it
> "I've worked in a fintech-adjacent platform handling payroll, bank account management, and salary processing. I'm experienced with column-level AES-256 encryption for PII (bank account numbers, tax file numbers), country-specific validation strategies for financial data, and wrapping financial operations in database transactions with explicit rollback to guarantee atomicity."

---

### 2. 🔴 Redis, Sidekiq, Kafka Experience

#### What the JD wants
> Redis, Sidekiq, Kafka (or similar) experience.

#### a) Sidekiq — Background Job Processing

**When to use it:** Any operation that shouldn't block the user's request — CSV processing, bulk deletions, payroll syncing, sending emails.

**Production-grade patterns you used:**

```ruby
class TodoItems::BulkDeleteWorker
  include Sidekiq::Worker

  RETRY_ATTEMPT = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_ATTEMPT'] || 3).to_i
  RETRY_DELAY   = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_DELAY'] || 3).to_i

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

**Key decisions to explain:**
| Decision | Why |
|----------|-----|
| `queue: :critical` | Named priority queues so critical jobs aren't starved by bulk email jobs |
| `retry: false` | Disable Sidekiq's default retry to implement custom exponential backoff |
| `perform_in(delay)` | Re-enqueue with increasing delay to handle race conditions (items not yet created) |
| ENV-configurable retries | Ops team can tune without code deploys |
| **Idempotency** | If items were already deleted, `deleted_count.zero?` just stops — no side effects |

#### b) Kafka — Event-Driven Architecture

**When to use it:** Decoupling services. When one system produces events that multiple other systems need to react to independently.

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
      payload_batch = []  # Reset on error — isolate poison messages
    end
    bulk_handle(payload_batch) if payload_batch.present?
  end
end
```

**Key patterns:**
- **Batch processing** — accumulate messages, flush to DB in bulk (configurable `bulk_handle_size`) → reduces DB writes dramatically
- **Offset management** — `mark_as_consumed(message)` tells Kafka "processed up to here" → at-least-once delivery guarantee
- **Poison message isolation** — if one message fails, catch error, commit offset (so it's not replayed), reset batch → bad data doesn't block the entire consumer
- **Observability** — Datadog traces wrap batch handling with per-type count tags

#### c) Redis — Caching Layer

```ruby
module CacheManagement::Services
  class Base < ::ServiceBase
    CACHE_EXPIRY_TIME = (ENV['CACHE_SERVICE_EXPIRE_TIME'] || 2).to_i.minutes

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

**Key patterns:**
- SHA256 digest of arguments for cache key uniqueness
- Dual-key strategy: `data_cache_key` + `invalidation_cache_key` → mark data stale without deleting
- ENV-configurable TTL

#### 💬 How to say it
> "I have hands-on production experience with all three. I use **Sidekiq** with named priority queues and custom retry logic for async processing. I've built **Kafka consumers** with batch processing, offset management, and poison message isolation. And I've implemented **Redis-backed caching** with SHA256 key digests and a dual-key invalidation strategy."

---

### 3. 🤖 AI-Assisted Coding/Debugging Tools

#### What the JD wants
> AI-assisted coding/debugging tools (Claude Code/Copilot/Cursor) with strong review discipline.

#### What you can say
> "I actively use AI coding assistants (Claude, Copilot) as part of my workflow for code generation, debugging, and exploring unfamiliar codebases. However, I maintain strict **review discipline** — I never merge AI-generated code without thorough review. I treat AI output as a first draft that needs human verification for correctness, edge cases, security implications, and alignment with the codebase's existing patterns and conventions. I also use AI to write RSpec tests and then manually verify the test logic covers meaningful cases, not just happy paths."

---

### 4. 🐳 Cloud Infrastructure and Containers

#### What the JD wants
> Cloud infrastructure and containers.

#### What you've worked with
The codebase uses **Docker** with multi-stage builds and **docker-compose** for local development with multiple interconnected services:

```yaml
# docker-compose.yml
services:
  eh_web:       # Rails app (port 3000)
  eh_rpc:       # gRPC server (port 50050)
  eh_postgres:  # PostgreSQL
  eh_redis:     # Redis (for Sidekiq + caching)
  eh_kafka:     # Kafka broker
```

The **Dockerfile** uses a multi-stage build optimized for CI/CD:
```dockerfile
FROM ehdevops/eh-base:2.3.0 AS assets-builder

# Docker secret mounts for credentials — never baked into image layers
RUN --mount=type=secret,id=bundle-gems-contribsys-com,env=BUNDLE_GEMS__CONTRIBSYS__COM \
    bundle install --jobs $(nproc) --retry 2

# Bootsnap precompilation for faster cold starts
RUN bundle exec bootsnap precompile --gemfile app/ lib/
```

**Key patterns:**
- **Multi-stage builds** to keep production images small
- **Docker secrets** (`--mount=type=secret`) for credentials — never stored in image layers
- **Bootsnap precompilation** in Docker for faster cold starts in production
- **Shared Docker networks** for inter-service communication (web ↔ gRPC ↔ Kafka)

#### 💬 How to say it
> "I work with Docker-containerized applications in production. I've used multi-stage Dockerfile builds to optimize image sizes, Docker secrets for credential management, and docker-compose to orchestrate multi-service environments (Rails, gRPC, Redis, PostgreSQL, Kafka). I understand how containers fit into a CI/CD pipeline."

---

### 5. 🔐 PCI-Aware Development Practices

#### What the JD wants
> PCI-aware development practices.

#### What you've done
While not a PCI auditor, you've practiced the **engineering side** of PCI compliance:

| PCI Principle | Your Practice |
|---------------|---------------|
| **Encrypt sensitive data** | `attr_encrypted` with AES-256-CBC for bank accounts, tax numbers, OAuth tokens |
| **Never log sensitive data** | Encryption keys from `ENV['ENCRYPTION_KEY']`, never hardcoded or logged |
| **Restrict access** | Policy Objects (`TodoItemPolicy`, `InstapayDashboardPolicy`) enforce authorization at every endpoint |
| **Audit trails** | `AuditHero::DSL::Auditable` + `paper_trail` track who changed what and when |
| **Input validation** | Country-specific validators (`BankAccountValidator::Au`, `::Uk`) + `injection: true` for SQL injection protection |
| **Secure secrets in containers** | Docker `--mount=type=secret` prevents credentials from leaking into image layers |

#### 💬 How to say it
> "I'm familiar with PCI-aware engineering practices. I encrypt sensitive financial data at rest using AES-256, manage encryption keys through environment variables (never in code), enforce authorization via Policy Objects at every endpoint, maintain audit trails for all financial data changes, and use Docker secrets to prevent credential leakage in container builds."

---

## Part B: What We Value — Behavioral & Mindset

---

### 6. 🏆 Strong Ownership & Accountability for Quality

#### What they want to hear
> Strong ownership and accountability for quality and release readiness.

#### How your code demonstrates this

**a) The ServiceBase Pattern = Built-in Quality Gates**
```ruby
class << self
  def call(*args, **kwargs)
    service_obj = new(*args, **kwargs)
    if service_obj.callable?
      service_obj.send(:before_call)     # Pre-flight checks
      call_result = service_obj.call
      service_obj.send(:after_call)      # Post-flight cleanup
      call_result
    end
  end
end
```

Every service has `before_call` / `after_call` hooks. You can add logging, telemetry, or validation **without touching the core logic**. `callable?` lets you gate execution behind feature flags for safe rollouts.

**b) Defensive Error Handling Everywhere**
```ruby
rescue ActiveRecord::RecordNotUnique => e
  @target_user = User.find_by(email: @target_user_email)
  @new_user = false
  Rails.logger.error(e)
```

Race conditions don't crash the system — they're anticipated and handled gracefully.

#### 💬 How to say it
> "I build quality into the architecture itself — lifecycle hooks for pre/post-flight checks, feature flags for safe rollouts, and defensive error handling for race conditions. I don't just write code that works in the happy path; I account for concurrent access, network failures, and partial states."

---

### 7. 🧊 Calm, Structured Risk-Based Thinking Under Pressure

#### What they want to hear
> Calm, structured risk-based thinking under delivery pressure.

#### Your examples

**Onboarding flow — phased tracing for risk isolation:**
```ruby
def call
  Datadog::Tracing.trace 'onboard.basic_info' do
    build_and_save_basic_info
    return if error?          # STOP early if risky phase fails
  end

  Datadog::Tracing.trace 'onboard.employment_and_payroll_detail' do
    create_employment_detail
    setup_positions
  end

  Datadog::Tracing.trace 'onboard.third_party' do
    populate_to_external_payroll
    notify_employee_added_event
  end
end
```

**Why this shows structured risk thinking:**
- Each phase is isolated in its own trace span → if production is slow, you know *exactly* which phase is the bottleneck
- `return if error?` after the riskiest phase (basic_info) → fail fast, don't cascade into third-party calls
- Third-party integrations are in their own phase → if external payroll is down, the employee is still created in your system

#### 💬 How to say it
> "I structure risky operations into isolated phases with explicit fail-fast checkpoints. If the core database write succeeds but a third-party integration fails, the user's data is still safe. I use APM tracing to instrument each phase so that under production pressure, I can pinpoint bottlenecks in minutes, not hours."

---

### 8. 💬 Clear Communication with Stakeholders

#### What they want to hear
> Clear communication with engineering, product, and business stakeholders.

#### How your codebase shows this

**a) Self-documenting service architecture:**
The folder structure tells the story:
```
app/services/membership/
├── creators/
│   ├── base.rb            # Shared onboarding logic
│   ├── onboard.rb         # Full employee onboarding
│   ├── quick_add.rb       # Lightweight add
│   ├── quick_add_freemium.rb
│   └── quick_add_freemium_csv.rb   # Bulk CSV import
├── transfer_membership.rb
└── self_change_email.rb
```

Any engineer (or product manager reading a PR) can understand **what each file does** from its name alone.

**b) Detailed parameter documentation in code:**
```ruby
# Options is a hash that can include:
# - onboarding_source: indicate the source [:onboarding, :job_adder, :csv, :payroll, :sap, :quick_add]
# - skip_contract: skipping onboarding contract step
# - placement: placement ID if acquired through job adder
# - employee_invitation_mail_metadata: metadata for the invitation email
```

#### 💬 How to say it
> "I believe clean architecture IS communication. I name files, classes, and methods so that anyone — junior dev, product manager, or on-call engineer at 2 AM — can understand the system. I document complex parameters inline and structure code as small, composable services rather than monolithic controllers."

---

### 9. 🔧 Practical Mindset — Improve Without Over-Engineering

#### What they want to hear
> Practical mindset: improve processes without over-engineering.

#### Your examples

**a) CSV Import — simple, pragmatic design:**
```ruby
CSV_HEADERS_MAPPING_COLUMNS = {
  'First name [*]': 'first_name',
  'Last name [*]': 'last_name',
  'Account email [*]': 'email',
  'Personal mobile number': 'personal_mobile_number'
}.freeze
```

A simple hash maps CSV headers to database columns. No fancy DSL, no configuration framework. Just a frozen hash that anyone can read and modify.

**b) Reusing ServiceBase instead of building a new framework:**
Every team in the codebase inherits from the same `ServiceBase`. Nobody built a "better" one. They extend it with concerns (`include Retryable`, `include Errors`) when needed.

**c) Policy Objects — just enough abstraction:**
```ruby
class TodoItemPolicy < ApplicationPolicy
  def access?
    member_state_access?(allow_postboarding: true) && feature_flag_enabled?
  end
end
```

17 lines. No abstract factory. No strategy pattern. Just a class with a method that returns true or false.

#### 💬 How to say it
> "I prefer simple, proven patterns over clever abstractions. A frozen hash for CSV mapping. A base class everyone shares. A policy that's 17 lines. I add complexity only when the business problem demands it — not because a design pattern book says I should."

---

## 📋 Quick Reference Cheat Sheet

| JD Requirement | Your Matching Experience | Key Keywords |
|----------------|--------------------------|--------------|
| Payment/Fintech | Bank accounts, payroll sync, InstaPay, salary management | AES-256, transactional integrity, financial data |
| Redis | Caching with SHA256 keys, dual-key invalidation | TTL, cache warming, key digests |
| Sidekiq | Priority queues, custom backoff, idempotent workers | `perform_in`, `queue: :critical`, ENV-config |
| Kafka | Batch consumers, offset management, poison message handling | Karafka, at-least-once, batch flush |
| AI tools | Claude, Copilot for code gen + debugging | Review discipline, first-draft mindset |
| Containers | Docker multi-stage builds, docker-compose orchestration | Secrets mount, bootsnap precompile |
| PCI-aware | Column-level encryption, audit trails, input validation | `attr_encrypted`, `AuditHero`, Policy Objects |
| Ownership | Lifecycle hooks, defensive error handling, feature flags | `before_call`, `callable?`, `RecordNotUnique` |
| Risk thinking | Phased tracing, fail-fast checkpoints, isolated third-party calls | Datadog spans, `return if error?` |
| Communication | Self-documenting architecture, inline docs, clean naming | Service folder structure, parameter docs |
| Practical | Frozen hashes, shared base classes, minimal policies | No over-engineering, just enough abstraction |
