# 📖 Real-World Backend Code Walkthrough — Learn by Reading Production Code

> This guide walks through **real production code files**, explaining every line like a mentor would.  
> Each section maps to a requirement from the fintech/payment senior role JD.  
> If you understand all of these, you can confidently discuss backend architecture in any interview.

---

## Table of Contents
1. [ServiceBase — The Foundation Pattern](#1-servicebase)
2. [TransferMembership — Transactions, Race Conditions, Service Composition](#2-transfer-membership)
3. [BankAccount Model — Encryption, Validation, Security](#3-bank-account)
4. [BulkDeleteWorker — Sidekiq Background Jobs](#4-bulk-delete-worker)
5. [TodoItemConsumer — Kafka Event Processing](#5-kafka-consumer)
6. [CacheManagement — Redis Caching](#6-cache-management)
7. [ScheduledTask — State Machine (AASM)](#7-state-machine)
8. [Onboard Service — Orchestration & Observability](#8-onboard-orchestration)
9. [gRPC Handler — Inter-Service Communication](#9-grpc)
10. [Dockerfile — Container & Cloud Infrastructure](#10-docker)
11. [CSV Processing — File Handling & Error Recovery](#11-csv-processing)

---

<a id="1-servicebase"></a>
## 1. 🏗️ ServiceBase — The Foundation Pattern

> **JD Match:** Practical mindset, strong ownership, code quality

### The Full Code

```ruby
# File: app/services/service_base.rb

# frozen_string_literal: true

class ServiceBase
  def initialize(*_args)
    @errors = []
  end

  class << self
    def call(*args, **kwargs)
      args = standardize_params(args)
      service_obj = new(*args, **kwargs)

      if service_obj.callable?
        service_obj.send(:before_call)
        call_result = service_obj.call
        service_obj.send(:after_call)
        call_result
      end
    end

    private

    def standardize_params(args)
      if args.length == 1 && args[0].is_a?(Hash)
        args[0] = args[0].with_indifferent_access
      end
      args
    end
  end

  attr_reader :errors

  def callable?
    true
  end

  def success?
    errors.blank?
  end

  def error?
    !success?
  end

  def error_messages
    return [] if errors.blank?
    errors&.each_with_object([]) do |error, error_messages|
      if error.is_a?(Exception)
        error_messages << error.message
      elsif error.is_a?(ActiveModel::Errors)
        error_messages.concat(error.full_messages)
      else
        error_messages << error.to_s
      end
    end
  end

  private

  def add_errors(errors)
    @errors ||= []
    @errors += Array(errors)
  end

  def add_error(error)
    @errors ||= []
    if error.is_a?(StandardError)
      @errors << error
    elsif error.is_a?(ActiveModel::Errors)
      @errors << error
    elsif error.is_a?(Array)
      error.each { |err| add_error(err) }
    else
      @errors << StandardError.new(error)
    end
  end

  def before_call; end
  def after_call; end
end
```

### Line-by-Line Explanation

**`frozen_string_literal: true`** (line 1)
- This is a Ruby "magic comment". It freezes all string literals in the file, making them immutable.
- **Why?** Performance. Ruby doesn't need to create a new String object every time. In a big app with thousands of files, this saves significant memory.

**`def initialize(*_args)` + `@errors = []`** (lines 4-6)
- Every service starts with an empty errors array. The `*_args` means "accept any arguments but don't use them here" — subclasses will define their own `initialize`.
- **Why an array, not exceptions?** Because a form might have 5 invalid fields. You want to tell the user ALL of them at once, not one-by-one.

**`class << self` block** (lines 8-30)
- This opens the "class-level" (static in other languages). `self.call` is a **class method** — you call it like `TransferMembership.call(args)` without doing `.new` first.
- **Why?** It gives you a clean one-liner API. The class method handles the boilerplate (instantiate → before_call → call → after_call) so every service has consistent lifecycle management.

**`service_obj.send(:before_call)`** (line 14)
- `.send` calls a private method by name. `before_call` is private, so normal code can't call it. But the ServiceBase framework CAN, because it's the framework's job.
- **Why private?** You don't want random external code calling `before_call`. Only the framework should control the lifecycle.

**`callable?` method** (lines 34-36)
- Returns `true` by default. A subclass can override this to return `false` if, for example, a feature flag is off.
- **Real use case:** You deploy a new service but it's behind a feature flag. You override `callable?` to check the flag — if it's off, the service silently does nothing.

**`success?` / `error?`** (lines 38-44)
- Simple: if the errors array is empty, the service succeeded. This is what callers check after `.call`.

**`add_error` method** (lines 74-86)
- It handles FOUR different types of errors: `StandardError`, `ActiveModel::Errors`, `Array`, and plain strings.
- **Why?** In a big app, errors come from everywhere — database validations return `ActiveModel::Errors`, other services return `StandardError`, sometimes you just have a string message. This normalizes all of them.

### What to say in an interview
> "Every complex operation in our codebase inherits from ServiceBase. It provides lifecycle hooks (`before_call`/`after_call`), error accumulation instead of exception-based flow, and a `callable?` guard for feature flags. This gives us consistent error handling across hundreds of services."

---

<a id="2-transfer-membership"></a>
## 2. 🔒 TransferMembership — Transactions, Race Conditions & Composition

> **JD Match:** Payment/fintech experience, PCI-aware practices, risk-based thinking

### The Full Code

```ruby
# File: app/services/membership/transfer_membership.rb

module Membership
  class TransferMembership < ServiceBase
    attr_reader :user, :target_user, :reset_password_token, :ip_addresses

    def initialize(member_uuid:, target_user_email:, ip_addresses:, user_agent: '')
      @member_uuid = member_uuid
      @target_user_email = target_user_email
      @user_agent = user_agent
      @ip_addresses = ip_addresses.join(',')
    end

    def call
      return self unless valid?

      @user = member.user
      @old_user_uuid = @user.uuid
      member.transaction do
        find_or_create_target_user
        generate_reset_password_token if success? && @new_user
        perform_transfer_membership if success?
        populate_to_external_payroll if success?

        if can_migrate_to_idp?(member.organisation)
          migrate_user_to_idp if success? && @new_user && !@target_user.idp_migrated?
        end

        migrate_ebf_login_identity if success?

        raise ActiveRecord::Rollback unless success?
      end

      self
    end

    private

    def valid?
      if member.nil?
        add_error(Membership::Errors::MemberNotFound.new(
          I18n.t('membership.not_found')
        ))
        return false
      end
      true
    end

    def member
      @member ||= Member.find_by(uuid: @member_uuid)
    end

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

    def perform_transfer_membership
      unless member.update(user_id: target_user.id)
        add_error(member.errors.full_messages)
      end

      if member.global_teams_employee?
        unless associated_global_teams_member.update(user_id: target_user.id)
          add_error(associated_global_teams_member.errors.full_messages)
        end
      end

      @user.reload
      transfer_hero_balance if success? && @user.memberships.count == 0
      @user.destroy! if success? && @user&.destroyable?
    end

    def migrate_ebf_login_identity
      if success?
        EbfUserMigration.create!(
          original_user_uuid: @old_user_uuid,
          target_user_uuid: @target_user.uuid,
          target_email: @target_user_email,
          flow: EbfUserMigration::FLOWS[:transfer_membership]
        )
      end
    end

    def transfer_hero_balance
      service = TransferHeroBalance.new(@member_uuid, target_user, ip_addresses)
      service.call
      unless service.success?
        add_error(service.errors)
      end
    end

    def can_migrate_to_idp?(organisation)
      @idp_auto_migrate_policy ||= IdpPolicy.new(AuthContext.new(organisation: organisation))
      @idp_auto_migrate_policy.access?
    end
  end
end
```

### Line-by-Line Deep Dive

#### 🔑 Keyword Arguments (line 7)
```ruby
def initialize(member_uuid:, target_user_email:, ip_addresses:, user_agent: '')
```
- The colons after each parameter name (`member_uuid:`) make them **keyword arguments**. You MUST name them when calling: `TransferMembership.new(member_uuid: "abc", target_user_email: "x@y.com", ...)`.
- `user_agent: ''` has a default — it's optional.
- **Why keyword args?** When a method has 4+ parameters, positional args become error-prone. Keyword args are self-documenting.

#### 🔒 Database Transaction (lines 19-32)
```ruby
member.transaction do
  find_or_create_target_user
  generate_reset_password_token if success? && @new_user
  perform_transfer_membership   if success?
  populate_to_external_payroll  if success?
  migrate_user_to_idp           if success? && @new_user && !@target_user.idp_migrated?
  migrate_ebf_login_identity    if success?

  raise ActiveRecord::Rollback unless success?
end
```

**Think of it like this:** Imagine you're at a bank. You want to transfer money from Account A to Account B. The steps are:
1. Debit Account A
2. Credit Account B
3. Send receipt

If step 2 fails, you MUST undo step 1. A **transaction** does this automatically — if anything inside the `do...end` block fails, ALL database changes inside it are undone.

**The `if success?` pattern:** Each step checks if the previous step worked. If `find_or_create_target_user` fails (adds an error), then `success?` returns `false`, so `generate_reset_password_token` is SKIPPED. This is called **Railway-Oriented Programming** — the code stays on the "success track" until something derails it to the "failure track".

**`raise ActiveRecord::Rollback unless success?`** (line 31): The final safety net. If ANY step failed, this line explicitly tells the database: "Undo everything you did inside this transaction block."

#### ⚡ Race Condition Handling (lines 55-70)
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

**The problem:** Two admins click "Transfer" at the same instant for the same email. Both check `User.find_by(email:)` — both get `nil`. Both try to create a new user. One succeeds, one hits the database's unique index constraint and gets `ActiveRecord::RecordNotUnique`.

**The solution:** 
1. Line 55: Try to find the user.
2. Lines 57-64: If not found, create them.
3. Lines 66-70: If a race condition causes `RecordNotUnique`, catch it, re-fetch the user that the OTHER process just created, and continue normally.

**Why not just use `find_or_create_by`?** Because that method has the exact same race condition. The `rescue` approach is the only truly safe pattern.

#### 💰 Financial Transfer (lines 89-94)
```ruby
def transfer_hero_balance
  service = TransferHeroBalance.new(@member_uuid, target_user, ip_addresses)
  service.call
  unless service.success?
    add_error(service.errors)
  end
end
```
This delegates to ANOTHER service object to move money (Hero Dollars). Notice:
- It passes `ip_addresses` — for **audit trail / fraud detection** (where did this transfer originate?).
- If the balance transfer fails, the error bubbles up, and the transaction rolls back — no partial money movement.

#### 🔐 Outbox Pattern (lines 96-106)
```ruby
def migrate_ebf_login_identity
  if success?
    EbfUserMigration.create!(
      original_user_uuid: @old_user_uuid,
      target_user_uuid: @target_user.uuid,
      target_email: @target_user_email,
      flow: EbfUserMigration::FLOWS[:transfer_membership]
    )
  end
end
```
This writes to an **outbox table** inside the same transaction. A separate worker picks up outbox records and processes them asynchronously. This is the **Transactional Outbox Pattern** — it guarantees that the message is sent if and only if the database transaction commits.

#### 🛡️ Policy Object + Memoization (lines 108-111)
```ruby
def can_migrate_to_idp?(organisation)
  @idp_auto_migrate_policy ||= IdpPolicy.new(AuthContext.new(organisation: organisation))
  @idp_auto_migrate_policy.access?
end
```
- `||=` is **memoization**. The first time this is called, it creates the policy object. The second time, it reuses the already-created one.
- **Why?** Creating a Policy might involve database queries. You don't want to do that twice per request.

---

<a id="3-bank-account"></a>
## 3. 💳 BankAccount Model — Encryption, Validation & Security

> **JD Match:** Payment/fintech, PCI-aware practices

### Key Sections Explained

#### Column-Level Encryption (lines 26-27)
```ruby
attr_encrypted :account_number, key: ENV['ENCRYPTION_KEY']
attr_encrypted :bsb, key: ENV['ENCRYPTION_KEY']
```

**What this does:** When you set `bank_account.account_number = "123456"`, the gem automatically encrypts it using AES-256 before saving to the database. When you read it, it decrypts automatically.

**What's stored in the database:** The actual column is `encrypted_account_number` containing gibberish like `"a8f2c3d1e5..."`. Even if someone steals the database, they can't read account numbers without the encryption key.

**`ENV['ENCRYPTION_KEY']`**: The key is stored as an environment variable — never in code, never in the repository. In production, this comes from AWS Secrets Manager or similar.

#### Country-Specific Validators (lines 69-74)
```ruby
validates_with BankAccountValidator::Au, if: proc { member.work_location_au? }
validates_with BankAccountValidator::Uk, if: proc { member.work_location_uk? }
validates_with BankAccountValidator::Sg, if: proc { member.work_location_sg? }
validates_with BankAccountValidator::My, if: proc { member.work_location_my? && member.my_localisation? }
validates_with BankAccountValidator::Ph, if: proc { ph_global_teams_employee? }
```

**Think of it like this:** An Australian bank account needs a 6-digit BSB number. A UK account needs a 6-digit sort code. A Singapore account doesn't need either. Instead of one massive `if/else` block, each country has its own validator class. The `if: proc { ... }` conditionally activates the right one.

**This is the Strategy Pattern** — each validator is a different "strategy" for validation, selected at runtime based on the member's country.

#### SQL Injection Protection (lines 47, 51, 58, 75)
```ruby
validates :account_name, :amount, injection: true, presence: true
validates :account_number, presence: true, injection: true
validates :bsb, injection: true
validates :statement_text, injection: true
```

**`injection: true`** is a custom validator that checks if the input contains SQL injection attempts (like `'; DROP TABLE users;--`). This is an **input sanitization layer** — defense in depth.

#### Hash for Change Detection (lines 236-242)
```ruby
def update_bsb_account_number_hash
  self.bsb_account_number_hash_salt = SecureRandom.hex
  self.bsb_account_number_hash = Digest::SHA256.hexdigest(
    "#{hash_bsb_account_number_input}#{bsb_account_number_hash_salt}"
  )
  self.bsb_account_number_changed = true
end
```

**What this does:** Creates a **salted hash** of the BSB + account number. This allows the system to detect if the account details changed WITHOUT decrypting the values. The salt (`SecureRandom.hex`) ensures that two identical account numbers produce different hashes (preventing rainbow table attacks).

#### Audit Trail (lines 4-6, 102)
```ruby
include AuditHero::DSL::Auditable
include AuditTrail::Auditable
include AuditTrail::BankAccountAuditable
# ... later ...
auditable
```

Every bank account change is audited — who changed it, when, from what value to what value. This is mandatory for financial compliance (PCI-DSS, SOX).

#### Soft Delete with Cached Data (lines 96, 110-116)
```ruby
before_destroy :cache_deleted_attributes, unless: :destroyed_by_deleting_member?

def cache_deleted_attributes
  DeletedBankAccount.create(
    account_number: account_number,
    bsb: bsb,
    member_id: member_id
  )
end
```

**Why?** When a bank account is deleted, the system saves a copy to `DeletedBankAccount` BEFORE destroying it. This is for reconciliation — if there's a dispute about a payment, you need to know what account was used even after the employee deleted it.

---

<a id="4-bulk-delete-worker"></a>
## 4. ⚙️ BulkDeleteWorker — Sidekiq Background Jobs

> **JD Match:** Redis, Sidekiq experience

### The Full Code
```ruby
# File: app/workers/todo_items/bulk_delete_worker.rb

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

### Every Line Explained

**`include Sidekiq::Worker`** (line 4): Makes this class a Sidekiq background job. Now it can be pushed to a Redis queue and processed asynchronously by a Sidekiq server process.

**`RETRY_ATTEMPT` and `RETRY_DELAY`** (lines 6-7):
```ruby
RETRY_ATTEMPT = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_ATTEMPT'] || 3).to_i
RETRY_DELAY = (ENV['TODO_ITEMS_BULK_DELETE_RETRY_DELAY'] || 3).to_i
```
- `ENV[...]` reads from environment variables. If the variable isn't set, `|| 3` provides a default of 3.
- `.to_i` converts the string to an integer.
- **Why ENV?** The ops team can change retry behavior in production without deploying code. Just update the environment variable and restart Sidekiq.

**`sidekiq_options queue: :critical, retry: false`** (line 9):
- `queue: :critical` — Sidekiq has multiple queues (`:critical`, `:default`, `:low`). Critical jobs get processed first. This ensures bulk deletions aren't stuck behind 10,000 email notification jobs.
- `retry: false` — Disables Sidekiq's built-in retry mechanism. **Why?** Because this worker implements its OWN retry logic with exponential backoff (see below). Using both would cause double-retries.

**`def perform(conditions_hash, retry_attempt = 0)`** (line 11):
- `perform` is the method Sidekiq calls when it picks this job from the queue.
- `conditions_hash` — tells the method WHAT to delete (e.g., `{ member_id: 123 }`).
- `retry_attempt = 0` — tracks how many times we've retried. Starts at 0.

**The retry logic** (lines 16-19):
```ruby
if deleted_count.zero? && retry_attempt < RETRY_ATTEMPT
  retry_attempt += 1
  self.class.perform_in((retry_attempt * RETRY_DELAY).minutes, conditions_hash, retry_attempt)
end
```

**Think of it like this:** You order food delivery and check the door. No food yet. Do you:
- (a) Check every 5 seconds for an hour? (wasteful)
- (b) Check after 3 min, then 6 min, then 9 min? (smart)

This code does (b) — **exponential backoff**:
- 1st retry: wait 3 minutes (`1 * 3`)
- 2nd retry: wait 6 minutes (`2 * 3`)
- 3rd retry: wait 9 minutes (`3 * 3`)
- After 3 retries: give up

`perform_in` schedules a FUTURE Sidekiq job — it doesn't block the current worker thread.

---

<a id="5-kafka-consumer"></a>
## 5. 📨 TodoItemConsumer — Kafka Event Processing

> **JD Match:** Kafka experience, risk-based thinking

### The Full Code
```ruby
# File: app/consumers/v2/todo_item_consumer.rb

class V2::TodoItemConsumer < ApplicationConsumer
  def consume
    payload_batch = []
    messages.each_with_index do |message, index|
      Karafka.logger.info "=== BEGIN: Consuming topic #{message.topic} - #{message} ==="
      payload = message.payload.presence

      next unless payload

      item_type = payload['item_type']
      payload_batch << payload

      if payload_batch.size >= bulk_handle_size
        bulk_handle(payload_batch)
        payload_batch = []
        mark_message(message)
      end
      Karafka.logger.info "=== END: Consuming topic #{message.topic} ==="
    rescue StandardError => e
      BugReporter.notify_index(e, { message: messages.payloads, payload_batch: payload_batch })
      mark_message(message)
      payload_batch = []
    end
    bulk_handle(payload_batch) if payload_batch.present?
  rescue StandardError => e
    BugReporter.notify_index(e, { message: messages.payloads })
  end

  private

  def bulk_handle(payload_batch)
    Datadog::Tracing.trace 'todo_item_consumer.bulk_handle' do |span|
      payload_batch.group_by { |payload| payload['item_type'] }.each do |item_type, payloads|
        span.set_tag("item_types.#{item_type}.count", payloads.size)
      end
      TodoItems::BulkHandler.new(payload_batch).handle
    end
  end

  def mark_message(message)
    Datadog::Tracing.trace 'todo_item_consumer.mark_message' do
      mark_as_consumed(message)
    end
  end

  def bulk_handle_size
    (ENV['TODO_ITEM_CONSUMER_BULK_HANDLE_SIZE'] || 10).to_i
  end
end
```

### Deep Dive

#### What is Kafka? (For beginners)
Imagine a post office. Instead of Service A calling Service B directly (like a phone call), Service A drops a letter in a mailbox (Kafka **topic**). Service B checks its mailbox whenever it's ready. Multiple services can have their own copy of the same letter.

#### Batch Processing (lines 5-19)
```ruby
payload_batch = []
messages.each_with_index do |message, index|
  payload_batch << payload

  if payload_batch.size >= bulk_handle_size
    bulk_handle(payload_batch)     # Process the entire batch at once
    payload_batch = []             # Reset for next batch
    mark_message(message)          # Tell Kafka "processed up to here"
  end
end
bulk_handle(payload_batch) if payload_batch.present?  # Don't forget leftovers!
```

**Why batch?** If you have 1,000 messages, doing 1,000 individual database inserts is SLOW. Batching them into groups of 10 means only 100 database calls. That's 10x faster.

#### Poison Message Handling (lines 22-25)
```ruby
rescue StandardError => e
  BugReporter.notify_index(e, { message: messages.payloads, payload_batch: payload_batch })
  mark_message(message)    # DON'T replay this bad message
  payload_batch = []       # Throw away the batch that might be contaminated
end
```

**What's a poison message?** A message with corrupted data that causes a crash every time you try to process it. Without this `rescue`:
1. Consumer crashes on bad message
2. Kafka replays the bad message (because it was never "consumed")
3. Consumer crashes again → infinite loop

**The fix:** Catch the error, report it, mark the offset (so Kafka moves past it), and reset the batch.

#### Observability (lines 34-40)
```ruby
def bulk_handle(payload_batch)
  Datadog::Tracing.trace 'todo_item_consumer.bulk_handle' do |span|
    payload_batch.group_by { |payload| payload['item_type'] }.each do |item_type, payloads|
      span.set_tag("item_types.#{item_type}.count", payloads.size)
    end
    TodoItems::BulkHandler.new(payload_batch).handle
  end
end
```

This wraps the processing in a **Datadog trace span**. It also tags the span with the count of each item type. In Datadog's dashboard, you can then see: "In the last hour, we processed 500 `leave_manager_review` items and 200 `task` items, and the batch took 120ms on average."

---

<a id="7-state-machine"></a>
## 7. 🔄 ScheduledTask — State Machine (AASM)

> **JD Match:** Structured risk-based thinking, quality

### The Key Section
```ruby
# File: app/models/scheduled_task.rb

class ScheduledTask < ApplicationRecord
  include AASM

  STATUSES = [PENDING = :pending,
               COMPLETED = :completed,
               DECLINED = :declined].freeze

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
end
```

### How State Machines Work (For Beginners)

**Think of a traffic light.** It can be GREEN, YELLOW, or RED. You can't go from GREEN directly to RED — you MUST go through YELLOW. A state machine enforces these rules in code.

**`aasm column: :status`** — The state is stored in the `status` column in the database.

**`state :pending, initial: true`** — When a new task is created, it starts as `pending`.

**`event :complete`** — Defines what happens when someone "completes" a task:
```ruby
event :complete do
  after { touch :completed_at }                    # After completing, set the timestamp
  transitions from: :pending, to: :completed       # ONLY pending tasks can be completed
end
```

If you try to complete a task that's already `declined`, AASM raises an error. You can NEVER go from `declined` → `completed`. The state machine prevents invalid states.

**`event :pending`** — Re-open a task:
```ruby
event :pending do
  before do
    self.completed_at = nil    # Clear the completion timestamp
  end
  transitions to: :pending
end
```

**Why is this better than `if/else`?**
Without AASM, you'd write:
```ruby
# BAD — scattered logic
def complete!
  if status == 'pending'
    update(status: 'completed', completed_at: Time.now)
  elsif status == 'declined'
    raise "Can't complete a declined task"
  end
end
```

With AASM, the rules are centralized, and you get automatic validation, callbacks, and query scopes (`ScheduledTask.pending`, `ScheduledTask.completed`).

### Advanced: Timezone-Aware Queries (lines 160-162)
```ruby
scope :due_today, lambda {
  joins(:organisation)
    .where("scheduled_tasks.due_date = DATE(now() at time zone organisations.time_zone)")
}
```

This is a PostgreSQL-level timezone conversion. If it's 11 PM in Australia but 2 PM in London, a task due "today" means different dates for different organisations. This scope handles that correctly by using each organisation's `time_zone` column.

---

<a id="8-onboard-orchestration"></a>
## 8. 🎯 Onboard Service — Orchestration & Observability

> **JD Match:** Ownership, risk-based thinking, clear communication

### The Call Method
```ruby
# File: app/services/membership/creators/onboard.rb

def call
  # PHASE 0: Validation
  validate_hook
  return if errors.present?

  validate_member_existence
  validate_members_limit
  validate_payroll_detail_params
  return if errors.present?

  # PHASE 1: Core data (wrapped in DB transaction internally)
  Datadog::Tracing.trace 'onboard.basic_info' do
    build_and_save_basic_info
    return if error?           # ← FAIL FAST: don't continue if core data fails
  end

  # PHASE 2: Employment details (depends on Phase 1)
  Datadog::Tracing.trace 'onboard.employment_and_payroll_detail' do
    create_employment_detail
    setup_positions
    create_payroll_detail
    build_pay_details
    Membership::TeamAssignment.call(member, member_team_ids)
  end

  # PHASE 3: Identity documents
  Datadog::Tracing.trace 'onboard.nric_detail' do
    build_nric_detail
    save_nric_detail
  end

  # PHASE 4: Benefits
  Datadog::Tracing.trace 'onboard.assign_benefits' do
    assign_benefits
    return if error?           # ← FAIL FAST again
  end

  # PHASE 5: Third-party integrations (most failure-prone)
  Datadog::Tracing.trace 'onboard.third_party' do
    populate_to_external_payroll
    add_note_to_job_adder
    notify_employee_added_event
  end

  # PHASE 6: Policies & checklists
  Datadog::Tracing.trace 'onboard.assign_policies_and_checklists' do
    assign_leave_policies
    assign_leave_categories
    trigger_onboarding_checklists
  end

rescue WorkLocationsCreateServiceError, IdpUserMigrationError => e
  errors << e.message
rescue StandardError => e
  issue_sentry_report(e, '#squad-index')
  errors << e.message
end
```

### Architecture Lesson: Phased Execution

**Why phases?** Each phase is wrapped in its own `Datadog::Tracing.trace` block. In production, if onboarding takes 10 seconds:
- Is it Phase 1 (database write) that's slow? → Fix database indexes
- Is it Phase 5 (third-party API) that's slow? → Add timeout, make it async

Without phases, you'd see "onboarding took 10 seconds" with no idea WHERE the time was spent.

**Fail-fast with `return if error?`:** After Phase 1 (basic_info) and Phase 4 (benefits), the code checks for errors and STOPS immediately. Why?
- If the core employee record couldn't be saved, there's no point trying to sync with payroll.
- But notice Phase 5 (third_party) does NOT have `return if error?` — a failure in Job Adder notification shouldn't prevent the employee from being onboarded.

**This is risk-based thinking:** Critical failures stop everything. Non-critical failures are captured but don't block the user.

---

<a id="9-grpc"></a>
## 9. 🌐 gRPC Handler — Inter-Service Communication

> **JD Match:** Clear communication, practical mindset

### The Full Code
```ruby
# File: app/grpc/rpc_handler/transfer_membership_handler.rb

module RpcHandler
  class TransferMembershipHandler < BaseHandler
    attr_reader :request, :meta

    def initialize(request, meta)
      @request = request
      @meta = meta
    end

    def call
      # 1. Delegate to the SAME service object used by REST APIs
      service = Membership::TransferMembership.new(
        member_uuid: request.member_id,
        target_user_email: request.target_user_email,
        ip_addresses: request.ip_addresses
      )
      service.call

      # 2. If success → return Protobuf response
      if service.success?
        return EhProtobuf::EmploymentHero::Membership::TransferMembershipResponse.new(
          user_id: service.target_user.uuid,
          reset_password_token: service.reset_password_token
        )
      end

      # 3. If failure → report error and return Protobuf error
      error = service.errors.first
      BugReporter.notify_konohagakure(error, {
        member_uuid: request.member_id,
        target_user_email: request.target_user_email
      })

      # 4. Map domain errors to gRPC standard errors
      if error.is_a?(Membership::Errors::MemberNotFound)
        EhProtobuf::NotFoundError.new(error.to_s)
      else
        EhProtobuf::InvalidArgumentError.new(error.to_s)
      end
    end
  end
end
```

### Key Architecture Insight

**The handler is just a THIN ADAPTER.** It does 3 things:
1. **Translate** the Protobuf request into Ruby keyword arguments
2. **Delegate** to the exact same `TransferMembership` service that REST endpoints use
3. **Translate** the Ruby result back into a Protobuf response

**Why this matters:** The business logic lives in ONE place (`TransferMembership`). Whether a request comes via REST API, gRPC, or a background worker, the same code runs. This is the **Hexagonal Architecture** principle — your core logic is transport-agnostic.

**Error mapping** (lines 36-40): Domain errors like `MemberNotFound` are mapped to standard gRPC error types (`NotFoundError` → HTTP 404 equivalent in gRPC). This gives calling services consistent, predictable error handling.

---

<a id="10-docker"></a>
## 10. 🐳 Dockerfile — Container & Cloud Infrastructure

> **JD Match:** Cloud infrastructure and containers

### The Full Code
```dockerfile
# File: Dockerfile

# ---- STAGE 1: Build ----
ARG BASE_IMAGE=ehdevops/eh-base:2.3.0
FROM $BASE_IMAGE AS assets-builder

ENV RAILS_ENV=production
ENV RACK_ENV=production

# Copy dependency files FIRST (for Docker layer caching)
COPY Gemfile Gemfile.lock Gemfile_next.lock ./

# Use Docker secrets for private gem credentials
RUN --mount=type=secret,id=bundle-gems-contribsys-com,env=BUNDLE_GEMS__CONTRIBSYS__COM \
    --mount=type=secret,id=bundle-gem-fury-io,env=BUNDLE_GEM__FURY__IO \
  bundle install --jobs $(nproc) --retry 2

COPY . ./

# Asset precompile with secrets mounted (not baked into image)
RUN --mount=type=secret,id=aws-access-key-id,env=AWS_ACCESS_KEY_ID \
    --mount=type=secret,id=aws-secret-access-key,env=AWS_SECRET_ACCESS_KEY \
  bundle exec rake assets:precompile

# Bootsnap precompilation for faster startup
RUN bundle exec bootsnap precompile --gemfile app/ lib/

# ---- STAGE 2: Run ----
FROM $BASE_IMAGE AS runner

ENV RAILS_ENV=production

# Run as non-root user for security
RUN useradd -U -u 1000 appuser
USER appuser

# Copy ONLY what we need from the build stage
COPY --chown=appuser:appuser --from=assets-builder /usr/local/bundle /usr/local/bundle
COPY --chown=appuser:appuser --from=assets-builder /app /app

EXPOSE 8080
```

### Key Concepts for Interviews

**Multi-stage build** (`AS assets-builder` → `AS runner`):
- Stage 1 has ALL build tools (git, compilers, dev dependencies)
- Stage 2 copies ONLY the compiled output
- Result: production image is much smaller (no build tools, no git history)

**Docker secrets** (`--mount=type=secret`):
- Credentials are mounted temporarily during the build step, then DISCARDED
- They never appear in any Docker image layer
- Even if someone `docker history` the image, secrets are not visible
- **This is PCI-relevant** — credentials must never be stored in container images

**Layer caching optimization**:
- `COPY Gemfile Gemfile.lock ./` is done BEFORE `COPY . ./`
- Why? Docker caches layers. If your code changes but Gemfile doesn't, Docker skips the slow `bundle install` step. This makes CI/CD builds much faster.

**Non-root user** (`USER appuser`):
- The container runs as `appuser`, not `root`
- If an attacker exploits a vulnerability, they don't have root access to the container
- **PCI compliance** requires principle of least privilege

---

<a id="11-csv-processing"></a>
## 11. 📄 CSV Processing — File Handling & Error Recovery

> **JD Match:** Practical mindset, ownership

### The Full Code
```ruby
# File: app/services/membership/creators/quick_add_freemium_csv.rb

module Membership::Creators
  class QuickAddFreemiumCsv < ServiceBase
    CSV_HEADERS_MAPPING_COLUMNS = {
      'First name [*]': 'first_name',
      'Last name [*]': 'last_name',
      'Account email [*]': 'email',
      'Personal mobile number': 'personal_mobile_number'
    }.freeze

    def initialize(current_membership, file_id, polling_key)
      @current_membership = current_membership
      @file_id = file_id
      @polling_key = polling_key
    end

    def call
      fetch_and_validate_csv_content    # Step 1: Get file & validate
      return self if error?

      create_new_members                # Step 2: Process rows
      return self if error?

      mark_file_as_used                 # Step 3: Publish event

      self
    end

    private

    def fetch_csv_content
      service = ::Files::GetFile.call(file_id, current_membership.uuid,
                                      current_membership.organisation.uuid)
      unless service.success?
        add_errors(service.errors)
        return
      end

      file_info = service.data
      if file_info.blank?
        add_errors(I18n.t('quick_add_freemium.errors.csv.file_not_found'))
        return
      end

      if ::Member::SUPPORTED_QUICK_ADD_FREEMIUM_FILE_TYPES.exclude?(file_info.file_type)
        add_errors(I18n.t('quick_add_freemium.errors.file_type_not_supported'))
        return
      end

      file_url = Files::Utils.expirable_url(file_info.file_url)
      @csv_content = CSV.parse(URI.parse(file_url).read, headers: true)
    rescue OpenURI::HTTPError, CSV::MalformedCSVError => e
      add_error "#{I18n.t('quick_add_freemium.errors.csv.cannot_open_remote_url')}: #{e.message}"
    rescue StandardError => e
      add_error(I18n.t('errors.messages.generic_error'))
      BugReporter.notify_elysium(e)
    end

    def validate_csv_headers
      file_csv_headers = @csv_content.headers.map(&:strip)
      allowed_csv_headers = CSV_HEADERS_MAPPING_COLUMNS.with_indifferent_access.keys

      if file_csv_headers.difference(allowed_csv_headers).any? ||
         allowed_csv_headers.difference(file_csv_headers).any?
        add_error(I18n.t('quick_add_freemium.errors.csv.invalid_headers'))
      end
    end

    def build_employees_params
      employees_params = []
      @csv_content.each do |row|
        current_employee_params = {}
        CSV_HEADERS_MAPPING_COLUMNS.with_indifferent_access.each do |csv_column, db_column|
          current_employee_params[db_column] = row[csv_column]
        end
        employees_params << current_employee_params
      end
      employees_params
    end

    def mark_file_as_used
      ::Buses::FileAssetBus.pick_up({ event: 'update', uuid: file_id })
    end
  end
end
```

### Architecture Lessons

**Defensive validation layers** (fetch_csv_content):
1. First check: Did the file service return successfully?
2. Second check: Does the file actually exist?
3. Third check: Is the file type supported (CSV, not .exe)?
4. Fourth check: Can we parse it as valid CSV?
5. Fifth check: Do the CSV headers match what we expect?

Each layer catches a different failure mode. A user uploading a renamed `.jpg` file as `.csv` will be caught at layer 4. A user with wrong column names is caught at layer 5.

**Mapping hash** (`CSV_HEADERS_MAPPING_COLUMNS`):
```ruby
CSV_HEADERS_MAPPING_COLUMNS = {
  'First name [*]': 'first_name',    # CSV column name → database column name
  'Last name [*]': 'last_name',
  'Account email [*]': 'email',
}.freeze
```

This is the simplest possible approach — no configuration framework, no DSL. Just a hash. `.freeze` makes it immutable so nobody accidentally modifies it at runtime. **This is practical mindset** — solving the problem without over-engineering.

**Event publishing** (mark_file_as_used):
```ruby
::Buses::FileAssetBus.pick_up({ event: 'update', uuid: file_id })
```

After processing, publish a Kafka event saying "this file has been used." Other services (like a file management service) can react by marking the file as processed. This is **event-driven architecture** — loose coupling between services.

---

## 📋 Summary: What Each File Teaches You

| File | Techniques Learned | JD Match |
|------|-------------------|----------|
| `service_base.rb` | Service Object pattern, error accumulation, lifecycle hooks, Template Method | Ownership, quality |
| `transfer_membership.rb` | DB transactions, race conditions (`RecordNotUnique`), Railway pattern, Outbox pattern | Payment/fintech, PCI, risk thinking |
| `bank_account.rb` | AES-256 encryption, salted hashing, Strategy pattern validators, audit trails, SQL injection protection | Payment/fintech, PCI-aware |
| `bulk_delete_worker.rb` | Sidekiq queues, custom exponential backoff, idempotency, ENV config | Redis/Sidekiq |
| `todo_item_consumer.rb` | Kafka batching, offset management, poison message handling, Datadog tracing | Kafka experience |
| `cache_management/base.rb` | SHA256 cache keys, dual-key invalidation, Redis TTL | Redis experience |
| `scheduled_task.rb` | AASM state machine, lifecycle callbacks, timezone-aware queries | Structured thinking |
| `onboard.rb` | Phased execution, fail-fast, Datadog APM spans, error isolation | Ownership, risk thinking |
| `transfer_membership_handler.rb` | gRPC thin adapter, Hexagonal Architecture, Protobuf error mapping | Clear communication |
| `Dockerfile` | Multi-stage builds, Docker secrets, non-root user, layer caching | Cloud/containers, PCI |
| `quick_add_freemium_csv.rb` | Defensive validation layers, header mapping, Kafka event publishing | Practical mindset |
