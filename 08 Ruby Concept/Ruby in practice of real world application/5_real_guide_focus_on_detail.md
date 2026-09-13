# Real-World Ruby Backend Flow — Mapped to Your Modules

A detailed walkthrough of backend patterns (Redis, Sidekiq, AASM, Transactions, Kafka, gRPC) traced through the actual codebase features you worked on.

---

## 1. Task Page → The Full Backend Flow (Most Directly Relevant)

> [!IMPORTANT]
> This is the **best interview example** because it touches every concept in one chain:
> **API → Redis → Sidekiq → Transaction → Lock → Policy → AASM → Email**

### Step 1: API Layer (Grape)

What happens when your frontend calls the API:

```ruby
# Your React component calls: POST /api/v3/members/tasks/bulk_complete
# That hits the Grape endpoint
```

### Step 2: Service Layer — `AsyncBulkCompleteTasks` (Redis + Sidekiq + Polling)

```ruby
# File: app/services/tasks/async_bulk_complete_tasks.rb
class Tasks::AsyncBulkCompleteTasks < Tasks::DestroyBase
  def call
    validate_task_ids              # Verify all task IDs belong to this org
    return self unless success?

    # 1. REDIS: Create a polling key for frontend to check progress
    @polling_key = "polling_bulk_complete_task_#{current_member.id}_#{SecureRandom.uuid}"
    service = RedisPollingServiceV2.new(polling_key)

    # 2. RACE CONDITION CHECK: Is someone already running a bulk complete?
    if service.in_progress?
      add_error(Tasks::Errors::CompleteInProgress.new)
      return self
    end

    # 3. REDIS: Mark as "processing"
    service.start!

    # 4. SIDEKIQ: Push the actual work to background
    ::Tasks::BulkCompleteTasksWorker.perform_async(task_ids, current_member.id, polling_key)
    # ↑ Returns immediately — your frontend gets the polling_key in <50ms
    self
  end
end
```

### Step 3: Sidekiq Worker — The Background Processor

```ruby
# File: app/workers/tasks/bulk_complete_tasks_worker.rb
class Tasks::BulkCompleteTasksWorker
  include Sidekiq::Worker
  sidekiq_options queue: :medium   # Not critical priority

  def perform(task_ids, member_id, polling_key)
    complete_service = Tasks::BulkCompleteTasks.new(task_ids: task_ids, member_id: member_id)
    complete_service.call

    # UPDATE Redis with result so your frontend polling gets the answer
    polling_service = RedisPollingServiceV2.new(polling_key)
    complete_service.success? ?
      polling_service.success!(true) :
      polling_service.error!(complete_service.error_messages)
  end
end
```

### Step 4: The Actual Service — Transaction + Policy + AASM

```ruby
# File: app/services/tasks/bulk_complete_tasks.rb
class Tasks::BulkCompleteTasks < ServiceBase
  def call
    return self unless valid_params?

    # DATABASE TRANSACTION: If any task fails, none are committed
    ScheduledTask.transaction do
      # PESSIMISTIC LOCKING: .lock prevents race conditions
      # If two admins click "bulk complete" at the same time for the same tasks,
      # the second one WAITS until the first finishes (SELECT ... FOR UPDATE)
      tasks = @current_member.organisation.scheduled_tasks
                             .where(id: @task_ids)
                             .lock.to_a  # ← This is the key!

      updated_task_ids = []

      tasks.each do |scheduled_task|
        # POLICY CHECK: Does this member have permission to update this task?
        unless policy(scheduled_task).update_task_status?
          add_error(build_error_message(scheduled_task.id, I18n.t('forbidden')))
          next  # Skip this task, continue with others
        end

        # AASM STATE MACHINE: scheduled_task.complete! is called inside here
        # This triggers the AASM transition: pending → completed
        # Which also triggers: touch :completed_at (sets timestamp)
        service = ::Tasks::ExecuteScheduledTask.new(
          scheduled_task, 'complete', { author: @current_member }
        )
        service.call

        if service.success?
          updated_task_ids << scheduled_task.id
        else
          add_error(build_error_message(scheduled_task.id, service.error_messages))
        end
      end

      # Send notification emails for all successfully completed tasks
      send_mail_updated_tasks(updated_task_ids)
    end
    self
  end

  private

  def policy(scheduled_task)
    @policy ||= ScheduledTaskPolicy.new(
      OpenStruct.new(current_member: @current_member, member: @current_member),
      scheduled_task
    )
  end
end
```

### Concepts Covered in This Flow

| Concept | Where It Happens |
|---|---|
| **Redis** | `RedisPollingServiceV2` — tracks "processing" / "completed" status |
| **Sidekiq** | `BulkCompleteTasksWorker` — runs in background, frontend doesn't wait |
| **Transaction** | `ScheduledTask.transaction do` — all-or-nothing database writes |
| **Race Condition** | `.lock.to_a` — pessimistic locking via `SELECT ... FOR UPDATE` |
| **Policy Check** | `policy(scheduled_task).update_task_status?` — authorization |
| **AASM** | `ExecuteScheduledTask` calls `scheduled_task.complete!` — state transition `pending → completed` |
| **Async Polling** | Frontend polls `/polling_status` with the key until Redis says "completed" |

---

## 2. Onboarding Flow → `Membership::Creators::Onboard`

The service behind the employee onboarding form:

```ruby
# File: app/services/membership/creators/onboard.rb
def call
  # VALIDATION: Check all inputs before doing anything
  validate_hook
  validate_member_existence
  validate_members_limit
  validate_offer_letter
  return if errors.present?

  # DATADOG TRACING: Each phase has its own span
  Datadog::Tracing.trace 'onboard.basic_info' do
    build_and_save_basic_info    # Save member + user + address + bank account
    return if error?
  end

  Datadog::Tracing.trace 'onboard.employment_and_payroll_detail' do
    create_employment_detail     # Job title, employment type
    setup_positions              # Positions (HR positions you added!)
    create_payroll_detail        # Pay details, classifications
    Membership::TeamAssignment.call(member, member_team_ids)
  end

  # GRPC: Call ATS service to prepare candidate data
  Datadog::Tracing.trace 'onboard.third_party' do
    populate_to_external_payroll  # Sync to KeyPay/Xero
    add_note_to_job_adder
    notify_employee_added_event
  end

  # This is where CHECKLISTS get triggered (your checklist page!)
  Datadog::Tracing.trace 'onboard.assign_policies_and_checklists' do
    assign_leave_policies
    trigger_onboarding_checklists  # Creates ScheduledTasks for the new employee
  end

  # GRPC CLIENT CALL to ATS service
  prepare_candidate_data  # → EhProtobuf::Ats::Client.prepare_candidate_data(...)

rescue StandardError => e
  issue_sentry_report(e, '#squad-index')  # SENTRY: Alert the team
  errors << e.message
end
```

---

## 3. Certification / Compliance → `MemberLicence` (AASM + Email + Kafka)

```ruby
# File: app/models/member_licence.rb
aasm(column: :status) do
  state :incomplete, initial: true   # Employee hasn't uploaded cert yet
  state :pending                     # Uploaded, waiting for approval
  state :active                      # Approved by manager
  state :expired                     # Past expiry date
  state :declined                    # Manager rejected

  event :approve do
    after do
      check_expiry_after_approve     # Edge case: approved but already expired
      EhNotification::MemberLicence.notify_status_changed(id)  # Push notification
    end
    transitions to: :active, from: [:pending], after: :acknowledge_log
  end

  event :expiry do
    before do
      clear_peo_verified
      recalculate_due_date
      publish_workflow_event_certification_expired  # KAFKA: Publish event
    end
    transitions to: :expired, after: :notify_expiry  # SIDEKIQ: Send email
  end
end
```

```ruby
# SIDEKIQ: Send email asynchronously via .delay
def send_assign_email
  return if member.terminated? || member.system?
  LicenceMailer.delay.assign(member_id, licence_id)  # ← .delay = Sidekiq
end
```

```ruby
# KAFKA: Auto-publish events after DB commit (configured in double_decker_bus.rb)
# MemberLicence is listed in the hooks — creates/updates/destroys auto-publish to Kafka

# DB QUERY OPTIMIZATION: Timezone-aware scopes
scope :expired_by_date, lambda {
  joins(member: :user)
    .where("member_licences.expiry_date <= DATE(:current_time at time zone users.time_zone)",
           current_time: Time.current)
}

# CALLBACK: Auto-delete todo items when certification is destroyed
register_destroy_dependent_todo_items_callback(
  item_type: [TodoItem::CERTIFICATION_EMPLOYEE_UPDATE, TodoItem::CERTIFICATION_APPROVER_REVIEW],
  original_item_id: :uuid
)
```

---

## 4. Contract Page → `Contract` (AASM with Named State Machine)

```ruby
# File: app/models/contract.rb

# NAMED STATE MACHINE: You can have multiple state machines on one model!
aasm(:clone_status) do    # ← :clone_status is the name, stored in a separate column
  state :document_ready, initial: true
  state :cloning_contents
  state :clone_failed

  event :clone_contents do
    transitions from: :document_ready, to: :cloning_contents
  end

  event :complete_clone do
    transitions from: :cloning_contents, to: :document_ready
  end

  event :fail_clone do
    transitions from: :cloning_contents, to: :clone_failed
  end
end
```

```ruby
# POSTGRESQL ARRAY OPERATIONS: Countries stored as PostgreSQL arrays
scope :by_country, lambda { |country|
  where("countries = ARRAY[]::varchar[] OR countries && ARRAY[?]::varchar[]", country)
}
# && is the PostgreSQL "overlap" operator — checks if two arrays share any elements
```

```ruby
# CTE (Common Table Expressions): Advanced SQL for searchable templates
scope :searchable_by_organisation, lambda { |organisation, query|
  ctes = {
    organisation_templates: by_organisation(organisation).merge(template_scope).select(:id),
    organisation_policies: by_organisation(organisation).merge(policy_scope).select(:id),
  }
  # Uses ANY(ARRAY(subquery)) to force INDEX SCAN instead of slower derived table plan
  id_filter = "id = ANY(ARRAY(#{union_cte_query}))"
  with(ctes).where(id: Contract.where(id_filter).ransack(name_cont: query).result)
}
```

---

## 5. Termination / Offboarding Flow

```ruby
# File: app/services/membership/service.rb (lines 177-200)
def terminate_member(generate_offboarding_task: true, email_payroll_admin: true, skip_populate_to_payroll: false)
  offboard_info = OffboardInfo.new({
    termination_date: termination_info[:termination_date],
    termination_type: termination_info[:termination_type],
    termination_reason: OffboardInfo::OTHER,
    comment: termination_info[:termination_reason]
  }).to_json

  # This triggers:
  # 1. AASM state change on member
  # 2. SIDEKIQ: Email to payroll admin
  # 3. GRPC: Sync to external payroll (KeyPay/Xero)
  # 4. KAFKA: Publish member updated event (via hooks)
  # 5. SIDEKIQ: Generate offboarding checklist tasks
  @member.terminate(offboard_info, modifier, generate_offboarding_task,
                    email_payroll_admin, skip_populate_to_payroll)
end
```

---

## Quick Map: Frontend Work → Backend Techniques

| Your Module | Backend File | Techniques |
|---|---|---|
| Task bulk complete | `bulk_complete_tasks.rb` | Transaction, `.lock` (race condition), Policy check, AASM `complete!` |
| Task bulk delete | `bulk_delete_worker.rb` | Sidekiq, custom retry with exponential backoff |
| Task async polling | `async_bulk_complete_tasks.rb` | Redis polling + Sidekiq worker |
| Employee onboarding | `onboard.rb` | Datadog tracing (6 phases), gRPC (`prepare_candidate_data`), Sentry |
| Onboard API | `employee_onboard.rb` | Grape API, Redis polling, Sidekiq `OnboardPollingWorker` |
| Certification/compliance | `member_licence.rb` | AASM (5 states, 5 events), Sidekiq (`.delay` emails), Kafka hooks, timezone-aware DB scopes |
| Contract types | `contract.rb` | Named AASM (`:clone_status`), PostgreSQL array ops (`&&`), CTEs |
| Termination flow | `membership/service.rb` | AASM transition, gRPC payroll sync, Kafka event, Sidekiq email |
| Onboard + classification | `membership/service.rb` | Nested attribute mapping, payroll detail sync, pay category |
| ScheduledTask model | `scheduled_task.rb` | AASM (pending/completed/declined), recurring tasks, timezone-aware queries |
| Kafka events | `double_decker_bus.rb` | `MemberLicence`, `Member`, `Organisation` auto-publish on `after_commit` |

> [!TIP]
> The **bulk complete tasks flow** is the best example to explain in an interview because it touches every single concept in one chain: **API → Redis → Sidekiq → Transaction → Lock → Policy → AASM → Email**.
