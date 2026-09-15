# 🏗️ System Design Walkthrough — Designing a New Backend Feature from Scratch

> This guide teaches you the **step-by-step thinking process** for designing a new backend module.
> Every code example follows the **exact conventions** from the Employment Hero codebase.
> We'll design a complete feature: **Employee Equipment Request** — from database to API.

---

## The 8 Steps of System Design

```
Step 1: Requirements & Scope       → "What are we building?"
Step 2: Database Design             → "What data do we store?"
Step 3: Model Layer                 → "What are the rules and state transitions?"
Step 4: Service Layer               → "Where does the business logic live?"
Step 5: Background Workers          → "What work is too slow for the API?"
Step 6: API Layer                   → "How does the frontend talk to us?"
Step 7: Cross-Cutting Concerns      → "Authorization, Observability, Events"
Step 8: Testing & Verification      → "How do we know it works?"
```

---

## The Example Feature: Employee Equipment Request

**Business requirement:** Employees can request equipment (laptop, monitor, keyboard). Their manager approves or declines. IT fulfills the order. Equipment is tracked until the employee returns it (termination/offboarding).

**Why this example?** It naturally touches ALL the patterns:
- AASM (request lifecycle: pending → approved → fulfilled → returned)
- Transaction (create request + reserve equipment atomically)
- Race condition (two people request the last laptop)
- Sidekiq (send email notifications asynchronously)
- Redis (cache equipment catalog, async polling for bulk operations)
- Kafka (publish events for asset tracking service)
- gRPC (check IT budget with finance service)
- Policy (who can approve, who can request)
- Feature flags (gradual rollout)
- Datadog (trace the approval flow)

---

## Step 1: Requirements & Scope

### 1.1 — List the user stories

Before writing ANY code, write down who does what:

```
As an EMPLOYEE, I can:
  - Request equipment from a catalog
  - See my pending/approved/declined requests
  - Cancel a pending request

As a MANAGER, I can:
  - See all requests from my direct reports
  - Approve or decline a request (with reason)
  - See a summary report of team equipment

As an IT ADMIN (system), I can:
  - Mark an approved request as "fulfilled" (equipment shipped)
  - Mark equipment as "returned" (employee left)
  - Bulk-fulfill multiple requests at once
```

### 1.2 — Identify the "nouns" (= database tables) and "verbs" (= service actions)

| Nouns (Tables) | Verbs (Services) |
|---|---|
| `EquipmentCategory` (Laptop, Monitor...) | `CreateEquipmentRequest` |
| `EquipmentItem` (individual MacBook #SN123) | `ApproveEquipmentRequest` |
| `EquipmentRequest` (the request itself) | `DeclineEquipmentRequest` |
| | `FulfillEquipmentRequest` |
| | `ReturnEquipment` |
| | `BulkFulfillRequests` |

### 1.3 — Identify which existing patterns apply

Look at the codebase for similar features. Ask:
- "Is there an existing model with a lifecycle?" → `MemberLicence` (certification), `ScheduledTask` (tasks)
- "Is there a bulk operation?" → `BulkCompleteTasks`, `BulkDeleteWorker`
- "Is there a request/approval flow?" → `WorkflowRequestApproval`

> [!TIP]
> **Interview tip:** When an interviewer asks "How would you design X?", START here. Show you think about requirements before jumping to code.

---

## Step 2: Database Design

### 2.1 — Draw the Entity-Relationship Diagram

```
┌──────────────────────┐     ┌──────────────────────────┐
│  equipment_categories│     │     equipment_items       │
│──────────────────────│     │──────────────────────────│
│ id (uuid, PK)        │◄────│ equipment_category_id(FK)│
│ name                 │     │ id (uuid, PK)            │
│ description          │     │ serial_number            │
│ organisation_id (FK) │     │ status (available/       │
│ created_at           │     │   assigned/retired)      │
│ updated_at           │     │ organisation_id (FK)     │
└──────────────────────┘     │ assigned_to_member_id(FK)│
                             │ created_at               │
                             └────────┬─────────────────┘
                                      │
┌─────────────────────────────────────┘
│
│     ┌──────────────────────────────────┐
│     │     equipment_requests           │
│     │──────────────────────────────────│
└─────│ equipment_item_id (FK, nullable) │ ← Assigned AFTER approval
      │ id (uuid, PK)                   │
      │ equipment_category_id (FK)      │ ← What type they want
      │ requester_id (FK → members)     │ ← Who is requesting
      │ approver_id (FK → members)      │ ← Who approved/declined
      │ organisation_id (FK)            │
      │ status (pending/approved/       │
      │   declined/fulfilled/returned)  │
      │ reason (text)                   │ ← Why they need it
      │ decline_reason (text)           │ ← Why manager declined
      │ approved_at (timestamp)         │
      │ fulfilled_at (timestamp)        │
      │ returned_at (timestamp)         │
      │ uuid (string, unique)           │
      │ created_at                      │
      │ updated_at                      │
      └──────────────────────────────────┘
```

### 2.2 — Write the Migration (following EH conventions)

```ruby
# File: db/migrate/20240901000001_create_equipment_requests.rb

# USE MigrationWithLockTimeout for safe production deploys!
# This prevents locking tables for minutes during deploy.
class CreateEquipmentRequests < MigrationWithLockTimeout
  def up
    create_table :equipment_categories, id: :uuid, default: 'uuid_generate_v4()' do |t|
      # ↑ Use UUID instead of auto-increment integer
      # WHY? UUIDs are globally unique — safe for distributed systems
      # The EH convention uses uuid_generate_v4() PostgreSQL function

      t.string :name, null: false
      t.text :description
      t.references :organisation, null: false, foreign_key: true
      t.timestamps
    end

    create_table :equipment_items, id: :uuid, default: 'uuid_generate_v4()' do |t|
      t.string :serial_number, null: false
      t.string :status, null: false, default: 'available'
      t.references :equipment_category, null: false, type: :uuid, foreign_key: true
      t.references :organisation, null: false, foreign_key: true
      t.references :assigned_to_member, type: :bigint, foreign_key: { to_table: :members }
      t.timestamps
    end

    create_table :equipment_requests, id: :uuid, default: 'uuid_generate_v4()' do |t|
      t.string :status, null: false, default: 'pending'
      t.text :reason
      t.text :decline_reason
      t.references :equipment_category, null: false, type: :uuid, foreign_key: true
      t.references :equipment_item, type: :uuid, foreign_key: true  # nullable — assigned after approval
      t.references :requester, null: false, type: :bigint, foreign_key: { to_table: :members }
      t.references :approver, type: :bigint, foreign_key: { to_table: :members }
      t.references :organisation, null: false, foreign_key: true
      t.string :uuid           # EH convention: separate UUID column for external references
      t.datetime :approved_at
      t.datetime :fulfilled_at
      t.datetime :returned_at
      t.timestamps
    end

    # INDEXES: Always add indexes on foreign keys and columns you filter/sort by
    add_index :equipment_items, :serial_number, unique: true
    add_index :equipment_items, :status
    add_index :equipment_requests, :status
    add_index :equipment_requests, :uuid, unique: true

    # COMPOSITE INDEX for common query: "my pending requests"
    add_index :equipment_requests, [:requester_id, :status]
  end

  def down
    drop_table :equipment_requests
    drop_table :equipment_items
    drop_table :equipment_categories
  end
end
```

### 2.3 — Why these design decisions?

| Decision | Why |
|---|---|
| UUID primary keys | Globally unique. Safe when syncing between microservices via Kafka/gRPC |
| `equipment_item_id` is nullable | The item is assigned AFTER approval. At request time, we only know the category |
| Separate `uuid` column | EH convention — gRPC/Kafka use UUID strings, not integer IDs |
| `MigrationWithLockTimeout` | Prevents table locks during production deploys (from Strong Migrations) |
| Composite index `[requester_id, status]` | The most common query is "Show me MY PENDING requests" — this makes it O(log n) |
| `up/down` instead of `change` | Required by `MigrationWithLockTimeout` — reversible migrations |

> [!IMPORTANT]
> **Interview tip:** "I always consider indexing strategy during schema design, not after. The most frequent queries should have composite indexes. I also use UUIDs for tables involved in cross-service communication."

---

## Step 3: Model Layer — Rules, Validations, State Machine

### 3.1 — The Model with AASM

```ruby
# File: app/models/equipment_request.rb
# frozen_string_literal: true

class EquipmentRequest < ApplicationRecord
  include AASM                         # State machine
  include AuditHero::DSL::Auditable    # Track every change (compliance)
  include UUIDGenerable                # Auto-generate UUID before create

  # === ASSOCIATIONS ===
  belongs_to :equipment_category
  belongs_to :equipment_item, optional: true  # Nullable — assigned after approval
  belongs_to :requester, class_name: 'Member'
  belongs_to :approver, class_name: 'Member', optional: true
  belongs_to :organisation

  # === VALIDATIONS ===
  validates :reason, presence: true
  validates :status, presence: true
  validates :decline_reason, presence: true, if: :declined?
  validate :requester_is_active_member

  # === SCOPES (efficient database queries) ===
  scope :pending, -> { where(status: :pending) }
  scope :by_requester, ->(member) { where(requester_id: member.id) }
  scope :by_organisation, ->(org) { where(organisation_id: org.id) }
  scope :for_manager, lambda { |manager|
    # Manager sees requests from their direct reports
    joins(:requester).where(members: { primary_manager_id: manager.id })
  }

  # === AUDIT TRAIL ===
  auditable

  # === STATE MACHINE ===
  # This is the HEART of the model — defines the entire lifecycle
  #
  # Diagram:
  #   pending ──approve──► approved ──fulfill──► fulfilled ──return──► returned
  #     │                                            │
  #     └──decline──► declined                       └──(auto on termination)
  #     │
  #     └──cancel──► cancelled
  #
  aasm(column: :status) do
    state :pending, initial: true
    state :approved
    state :declined
    state :fulfilled
    state :returned
    state :cancelled

    event :approve do
      # BEFORE: Check if there's available equipment in this category
      before do
        validate_equipment_availability!
      end

      # AFTER: Send notification to employee + IT team
      after do
        self.approved_at = Time.current
        save!
        # SIDEKIQ: Send email asynchronously (don't block the API response)
        EquipmentRequestMailer.delay.approved(id)
        EhNotification::EquipmentRequest.notify_approved(id)
      end

      transitions from: :pending, to: :approved
    end

    event :decline do
      after do
        EquipmentRequestMailer.delay.declined(id)
        EhNotification::EquipmentRequest.notify_declined(id)
      end

      transitions from: :pending, to: :declined
    end

    event :fulfill do
      before do
        raise 'Equipment item must be assigned before fulfilling' if equipment_item_id.blank?
      end

      after do
        self.fulfilled_at = Time.current
        save!
        EquipmentRequestMailer.delay.fulfilled(id)
      end

      transitions from: :approved, to: :fulfilled
    end

    event :return_equipment do
      after do
        self.returned_at = Time.current
        equipment_item.update!(status: 'available', assigned_to_member_id: nil)
        save!
      end

      transitions from: :fulfilled, to: :returned
    end

    event :cancel do
      transitions from: :pending, to: :cancelled
    end
  end

  private

  def requester_is_active_member
    if requester&.terminated?
      errors.add(:requester, I18n.t('equipment_requests.requester_terminated'))
    end
  end

  def validate_equipment_availability!
    available_count = EquipmentItem
      .where(equipment_category_id: equipment_category_id, status: 'available')
      .count

    if available_count.zero?
      raise AASM::InvalidTransition, I18n.t('equipment_requests.no_equipment_available')
    end
  end
end
```

### 3.2 — Why these design decisions?

| Pattern | What it does | Real EH example |
|---|---|---|
| `include AASM` | Prevents invalid transitions (can't fulfill a declined request) | `MemberLicence`, `ScheduledTask` |
| `.delay.approved(id)` | Sends email via Sidekiq — API returns instantly | `LicenceMailer.delay.assign(member_id, licence_id)` |
| `include AuditHero::DSL::Auditable` | Every status change is logged for compliance | Every model with `auditable` |
| `include UUIDGenerable` | Auto-generates UUID for gRPC/Kafka | `Contract`, `MemberLicence` |
| `validate :requester_is_active_member` | Business rule: terminated employees can't request equipment | `MemberLicence` checks `member.terminated?` |

---

## Step 4: Service Layer — Business Logic

### 4.1 — Approve Service (Transaction + Race Condition + gRPC)

```ruby
# File: app/services/equipment_requests/approve.rb
# frozen_string_literal: true

module EquipmentRequests
  class Approve < ServiceBase
    def initialize(request_id:, approver:)
      super()
      @request_id = request_id
      @approver = approver
    end

    def call
      # STEP 1: Find the request
      @request = EquipmentRequest.find_by(id: @request_id)
      unless @request
        add_error(StandardError.new(I18n.t('equipment_requests.not_found')))
        return self
      end

      # STEP 2: Policy check — does this manager have permission to approve?
      unless policy.approve?
        add_error(StandardError.new(I18n.t('forbidden')))
        return self
      end

      # STEP 3: Check budget with Finance service via gRPC
      Datadog::Tracing.trace 'equipment_request.budget_check' do
        check_budget!
        return self if error?
      end

      # STEP 4: Transaction + Race Condition handling
      Datadog::Tracing.trace 'equipment_request.approve_and_assign' do
        approve_and_assign_equipment!
      end

      self
    rescue StandardError => e
      BugReporter.notify(e, request_id: @request_id, approver_id: @approver.id)
      add_error(e)
      self
    end

    private

    # === gRPC: Check budget with Finance microservice ===
    def check_budget!
      response = EhProtobuf::Finance::Client.check_equipment_budget(
        EhProtobuf::Finance::CheckEquipmentBudgetRequest.new(
          organisation_id: @request.organisation.uuid,
          category_id: @request.equipment_category.uuid,
          amount: 1
        )
      )

      unless response.success?
        add_error(StandardError.new(I18n.t('equipment_requests.budget_exceeded')))
      end
    end

    # === Transaction + Pessimistic Locking ===
    def approve_and_assign_equipment!
      ActiveRecord::Base.transaction do
        # PESSIMISTIC LOCK: Lock the available equipment items
        # This prevents two managers from assigning the same laptop
        # at the exact same millisecond.
        #
        # SELECT * FROM equipment_items WHERE ... FOR UPDATE
        # ↑ Any other transaction trying to read these rows WAITS
        available_item = EquipmentItem
          .where(
            equipment_category_id: @request.equipment_category_id,
            status: 'available'
          )
          .lock       # ← FOR UPDATE lock
          .first      # ← Take the first available

        if available_item.nil?
          add_error(StandardError.new(I18n.t('equipment_requests.no_equipment_available')))
          raise ActiveRecord::Rollback
        end

        # Assign the equipment to the requester
        available_item.update!(
          status: 'assigned',
          assigned_to_member_id: @request.requester_id
        )
        @request.equipment_item = available_item
        @request.approver = @approver

        # AASM: pending → approved (triggers after callbacks: email, notification)
        @request.approve!

      rescue ActiveRecord::RecordNotUnique => _e
        # RACE CONDITION: Another process assigned the same item
        # between our SELECT and UPDATE. Retry with the next available item.
        retry
      end
    end

    def policy
      @policy ||= EquipmentRequestPolicy.new(
        AuthContext.new(current_member: @approver),
        @request
      )
    end
  end
end
```

### 4.2 — Why Transaction + Lock together?

Here's the race condition **without** locking:

```
Time  | Manager A                          | Manager B
------|------------------------------------|----------------------------------
t=1   | Find available laptop #1           |
t=2   |                                    | Find available laptop #1 (same!)
t=3   | Assign laptop #1 to Employee X     |
t=4   |                                    | Assign laptop #1 to Employee Y ← CONFLICT!
```

With `.lock`:
```
Time  | Manager A                          | Manager B
------|------------------------------------|----------------------------------
t=1   | Lock laptop #1 (SELECT FOR UPDATE) |
t=2   |                                    | Try to lock laptop #1 → BLOCKED, waiting...
t=3   | Assign laptop #1 to Employee X     |
t=4   | COMMIT (release lock)              |
t=5   |                                    | Lock released → laptop #1 is now 'assigned'
t=6   |                                    | No available items → finds laptop #2 instead
```

---

## Step 5: Background Workers

### 5.1 — Bulk Fulfill Worker (Sidekiq + Redis Polling)

Following the **exact same pattern** as `BulkCompleteTasksWorker`:

```ruby
# File: app/services/equipment_requests/async_bulk_fulfill.rb
# This is what the API calls — it returns INSTANTLY with a polling key

class EquipmentRequests::AsyncBulkFulfill < ServiceBase
  def initialize(request_ids:, admin:)
    super()
    @request_ids = request_ids
    @admin = admin
  end

  def call
    validate_request_ids!
    return self if error?

    # REDIS: Create polling key for frontend
    @polling_key = "polling_bulk_fulfill_equipment_#{@admin.id}_#{SecureRandom.uuid}"
    polling_service = RedisPollingServiceV2.new(@polling_key)

    # RACE CONDITION: Prevent double-submit
    if polling_service.in_progress?
      add_error(EquipmentRequests::Errors::FulfillInProgress.new)
      return self
    end

    # REDIS: Mark as "processing"
    polling_service.start!

    # SIDEKIQ: Push to background
    EquipmentRequests::BulkFulfillWorker.perform_async(
      @request_ids, @admin.id, @polling_key
    )

    self
  end

  attr_reader :polling_key

  private

  def validate_request_ids!
    count = @admin.organisation.equipment_requests
                  .where(id: @request_ids, status: 'approved')
                  .count
    if count != @request_ids.length
      add_error(EquipmentRequests::Errors::InvalidRequestIds.new)
    end
  end
end
```

```ruby
# File: app/workers/equipment_requests/bulk_fulfill_worker.rb

class EquipmentRequests::BulkFulfillWorker
  include Sidekiq::Worker
  sidekiq_options queue: :medium  # Not blocking user-facing work

  def perform(request_ids, admin_id, polling_key)
    service = EquipmentRequests::BulkFulfill.new(
      request_ids: request_ids, admin_id: admin_id
    )
    service.call

    # Update Redis so the frontend polling gets the result
    polling_service = RedisPollingServiceV2.new(polling_key)
    service.success? ?
      polling_service.success!(service.results) :
      polling_service.error!(service.error_messages)
  end
end
```

```ruby
# File: app/services/equipment_requests/bulk_fulfill.rb

class EquipmentRequests::BulkFulfill < ServiceBase
  def call
    EquipmentRequest.transaction do
      requests = EquipmentRequest
                   .where(id: @request_ids, status: 'approved')
                   .lock.to_a  # PESSIMISTIC LOCK — prevent race conditions

      requests.each do |request|
        # Assign equipment item
        item = find_available_item(request.equipment_category_id)

        if item.nil?
          add_error("No available #{request.equipment_category.name} for request ##{request.id}")
          next
        end

        item.update!(status: 'assigned', assigned_to_member_id: request.requester_id)
        request.equipment_item = item

        # AASM transition: approved → fulfilled
        request.fulfill!
      end
    end

    self
  end
end
```

### 5.2 — Expiry Check Cron Worker

```ruby
# File: app/workers/equipment_requests/auto_return_terminated_worker.rb
# Runs daily via cron — returns equipment from terminated employees

class EquipmentRequests::AutoReturnTerminatedWorker
  include Sidekiq::Worker
  sidekiq_options queue: :low, retry: 3

  def perform
    # Find fulfilled requests where the employee has been terminated
    EquipmentRequest
      .where(status: 'fulfilled')
      .joins(:requester)
      .where(members: { terminated: true })
      .find_each do |request|
        # AASM: fulfilled → returned
        request.return_equipment!
      rescue AASM::InvalidTransition => e
        BugReporter.notify(e, request_id: request.id)
      end
  end
end
```

---

## Step 6: API Layer (Grape)

### 6.1 — The Endpoint

```ruby
# File: app/api/employment_hero/v3/organisations/equipment_requests.rb
# frozen_string_literal: true

module EmploymentHero::V3::Organisations
  class EquipmentRequests < Grape::API
    resource :equipment_requests do

      # === LIST REQUESTS ===
      desc 'List equipment requests for current member'
      params do
        optional :status, type: String, values: %w[pending approved declined fulfilled returned]
        optional :page, type: Integer, default: 1
        optional :per_page, type: Integer, default: 20
      end
      get do
        # POLICY: Only show requests this member is allowed to see
        requests = policy_scope(EquipmentRequest)
                     .by_organisation(current_organisation)

        requests = requests.where(status: params[:status]) if params[:status].present?
        requests = requests.page(params[:page]).per(params[:per_page])

        # SERIALIZER: Transform ActiveRecord objects into JSON
        GoogleJsonResponse.render(
          requests,
          each_serializer: EquipmentRequestSerializer,
          meta: { total_count: requests.total_count }
        )
      end

      # === CREATE REQUEST ===
      desc 'Create a new equipment request'
      params do
        requires :equipment_category_id, type: String
        requires :reason, type: String
      end
      post do
        # FEATURE FLAG: Only available if the feature is enabled
        error!('Feature not available', 403) unless feature_flag_on?(:equipment_requests)

        service = ::EquipmentRequests::Create.new(
          requester: current_membership,
          equipment_category_id: params[:equipment_category_id],
          reason: params[:reason]
        )
        service.call

        if service.success?
          GoogleJsonResponse.render(service.request, serializer: EquipmentRequestSerializer)
        else
          error!(service.error_messages, 422)
        end
      end

      # === APPROVE REQUEST ===
      desc 'Approve an equipment request'
      route_param :id do
        put :approve do
          service = ::EquipmentRequests::Approve.new(
            request_id: params[:id],
            approver: current_membership
          )
          service.call

          if service.success?
            GoogleJsonResponse.render(service.request, serializer: EquipmentRequestSerializer)
          else
            error!(service.error_messages, 422)
          end
        end
      end

      # === BULK FULFILL (Async with polling) ===
      desc 'Bulk fulfill equipment requests'
      params do
        requires :request_ids, type: Array[String]
      end
      post :bulk_fulfill do
        service = ::EquipmentRequests::AsyncBulkFulfill.new(
          request_ids: params[:request_ids],
          admin: current_membership
        )
        service.call

        if service.success?
          # Return polling key — frontend will poll for status
          GoogleJsonResponse.render({ polling_key: service.polling_key })
        else
          error!(service.error_messages, 422)
        end
      end

      # === POLLING STATUS ===
      desc 'Check bulk fulfill status'
      get :polling_status do
        polling_service = RedisPollingServiceV2.new(params[:polling_key])
        GoogleJsonResponse.render(
          status: polling_service.state,
          errors: polling_service.content&.dig(:errors),
          data: polling_service.content&.dig(:data)
        )
      end

    end
  end
end
```

### 6.2 — The flow diagram (Frontend → Backend)

```
Your React Component                API (Grape)               Service              Sidekiq Worker        Redis
      │                               │                         │                       │                 │
      │── POST /equipment_requests -->│                         │                       │                 │
      │                               │── Create.new().call --->│                       │                 │
      │                               │                         │── validate + save     │                 │
      │                               │                         │── publish Kafka event │                 │
      │<── 201 { request } -----------│                         │                       │                 │
      │                               │                         │                       │                 │
      │── PUT /approve/:id ---------->│                         │                       │                 │
      │                               │── Approve.new().call -->│                       │                 │
      │                               │                         │── gRPC: check budget  │                 │
      │                               │                         │── transaction + lock  │                 │
      │                               │                         │── AASM: approve!      │                 │
      │                               │                         │── .delay.approved()   │                 │
      │<── 200 { request } -----------│                         │                       │                 │
      │                               │                         │                       │── send email    │
      │                               │                         │                       │                 │
      │── POST /bulk_fulfill -------->│                         │                       │                 │
      │                               │── AsyncBulkFulfill ---->│                       │                 │
      │                               │                         │── polling_key ------->│<--- start! ---->│
      │                               │                         │── perform_async ----->│                 │
      │<── { polling_key } -----------│                         │                       │── BulkFulfill   │
      │                               │                         │                       │── success!() -->│
      │── GET /polling_status ------->│                         │                       │                 │
      │<── { status: "completed" } ---│<--------------------------------------------- Redis.get -------->│
```

---

## Step 7: Cross-Cutting Concerns

### 7.1 — Policy (Authorization)

```ruby
# File: app/policies/equipment_request_policy.rb
# Following the EXACT pattern from ScheduledTaskPolicy

class EquipmentRequestPolicy < ApplicationPolicy

  def index?
    member_state_access?(allow_postboarding: true) && feature_flag?
  end

  def create?
    member_state_access?(allow_postboarding: true) &&
      feature_flag? &&
      !current_member.terminated?
  end

  def approve?
    return false unless feature_flag?
    return false unless record.present?
    return true if admin_or_owner?

    # Manager can approve their direct report's requests
    record.requester.primary_manager_id == current_member.id
  end

  def fulfill?
    admin_or_owner? && feature_flag?
  end

  def cancel?
    feature_flag? && record.requester_id == current_member.id && record.pending?
  end

  private

  def feature_flag?
    compute_permission(
      entitlement: :equipment_requests_entitlement,
      legacy_logic: -> { feature_flag_on?(:equipment_requests) }
    )
  end
end
```

### 7.2 — Kafka Event Publishing

In `config/initializers/double_decker_bus.rb`, add `EquipmentRequest` to the hooks:

```ruby
# This automatically publishes a Kafka event whenever
# an EquipmentRequest is created, updated, or destroyed.
# Other services (asset tracking, reporting) consume these events.

config.hook_settings = {
  hooks: [
    {
      bus: 'basic_info',
      events: %i[create update],
      callback: :after_commit,  # ONLY after transaction commits!
      models: [
        Organisation, Member, User,
        EquipmentRequest,  # ← ADD THIS
        # ... existing models
      ],
    }
  ]
}
```

### 7.3 — gRPC Handler (if OTHER services need to query equipment)

```ruby
# File: app/grpc/rpc_handler/get_member_equipment_handler.rb

module RpcHandler
  class GetMemberEquipmentHandler < BaseHandler
    def initialize(request, meta)
      @request = request
      @meta = meta
    end

    def call
      member = Member.find_by(uuid: request.member_id)

      unless member
        return EhProtobuf::NotFoundError.new(
          I18n.t('sync_errors.member_with_uuid_not_found', member_uuid: request.member_id)
        )
      end

      equipment = member.equipment_requests
                        .where(status: 'fulfilled')
                        .includes(:equipment_item, :equipment_category)

      EhProtobuf::EmploymentHero::GetMemberEquipmentResponse.new(
        items: equipment.map do |req|
          EhProtobuf::EmploymentHero::EquipmentInfo.new(
            category_name: req.equipment_category.name,
            serial_number: req.equipment_item.serial_number,
            assigned_at: date_format(req.fulfilled_at)
          )
        end
      )
    end
  end
end
```

### 7.4 — Feature Flag (Gradual Rollout)

```ruby
# In config/initializers/feature_flag_assistant.rb (for local dev):
FeatureFlagAssistant.stub!(
  equipment_requests: false,  # Disabled in dev by default
  # ... existing flags
)

# In production, enable via the Feature Flag service:
# 1. Create flag "equipment_requests" in the Feature Flag dashboard
# 2. Enable for specific organisations first (Beta testing)
# 3. Enable for all Australian orgs
# 4. Enable globally
```

---

## Step 8: Testing & Verification

### 8.1 — RSpec for the Service (following EH conventions)

```ruby
# File: spec/services/equipment_requests/approve_spec.rb
# frozen_string_literal: true

RSpec.describe EquipmentRequests::Approve do
  let(:organisation) { create(:organisation) }
  let(:manager) { create(:member, :admin, organisation: organisation) }
  let(:employee) { create(:member, organisation: organisation, primary_manager: manager) }
  let(:category) { create(:equipment_category, organisation: organisation) }
  let!(:available_item) { create(:equipment_item, equipment_category: category, status: 'available') }
  let(:request) { create(:equipment_request, requester: employee, equipment_category: category) }

  subject { described_class.new(request_id: request.id, approver: manager) }

  describe '#call' do
    context 'when successful' do
      it 'transitions to approved and assigns equipment' do
        subject.call

        expect(subject).to be_success
        expect(request.reload.status).to eq('approved')
        expect(request.equipment_item).to eq(available_item)
        expect(available_item.reload.status).to eq('assigned')
      end
    end

    context 'when no equipment available (race condition)' do
      before { available_item.update!(status: 'assigned') }

      it 'returns an error' do
        subject.call
        expect(subject).not_to be_success
        expect(subject.error_messages).to include(/no.*available/i)
      end
    end

    context 'when unauthorized' do
      let(:random_member) { create(:member, organisation: organisation) }
      subject { described_class.new(request_id: request.id, approver: random_member) }

      it 'returns forbidden' do
        subject.call
        expect(subject).not_to be_success
        expect(subject.error_messages).to include(/forbidden/i)
      end
    end
  end
end
```

### 8.2 — Verification Checklist

```markdown
- [ ] Migration runs without locking tables (MigrationWithLockTimeout)
- [ ] AASM transitions are correct (can't fulfill a pending request)
- [ ] Race condition: two managers can't assign the same equipment
- [ ] Policy: only managers can approve their reports' requests
- [ ] Sidekiq: emails are sent asynchronously
- [ ] Redis polling: bulk fulfill returns status correctly
- [ ] Feature flag: feature is gated behind flag
- [ ] Kafka: events are published after commit
- [ ] Sentry: errors are reported with context
- [ ] Datadog: each phase has its own trace span
```

---

## 📋 Interview Cheat Sheet — The 8-Step Framework

When an interviewer says "Design a system for X", follow this:

| Step | What to say | Time |
|---|---|---|
| **1. Requirements** | "First, let me clarify the user stories and identify the entities..." | 2 min |
| **2. Database** | "I'd design the schema with UUIDs, proper indexes, and foreign keys..." | 3 min |
| **3. Models** | "The core model has a state machine with validations and callbacks..." | 3 min |
| **4. Services** | "Business logic lives in service objects with transactions and locking..." | 5 min |
| **5. Workers** | "Heavy operations run in Sidekiq with Redis polling for frontend..." | 3 min |
| **6. API** | "The Grape API is thin — just validation and delegation to services..." | 2 min |
| **7. Cross-cutting** | "Authorization via policy objects, events via Kafka, monitoring via Datadog..." | 3 min |
| **8. Testing** | "I verify with RSpec covering happy path, race conditions, and auth..." | 2 min |

> [!TIP]
> **The key insight:** The API layer is THIN. It validates params and delegates to services. Services contain business logic, use transactions for data integrity, and push slow work to Sidekiq. This is the same pattern across the entire Employment Hero codebase — from task management to employee onboarding to certification compliance.
