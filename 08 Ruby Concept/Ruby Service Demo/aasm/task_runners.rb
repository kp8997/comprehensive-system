require 'aasm'

class TaskRunner
  include AASM

  attr_accessor :retry_count, :error_log

  aasm(:payment, column: :status, namespace: :payment) do
    state :idle, initial: true
    state :running
    state :completed
    state :failed

    # Event with guards and parameter passing
    event :run do
      transitions from: :idle, to: :running
    end

    event :complete do
      transitions from: :running, to: :completed
    end

    event :fail do
      # Guards can check instance state directly
      transitions from: :running, to: :failed, 
                  guard: :exhausted_retries?, 
                  after: :log_failure
      
      # Fallback transition if guard fails: reset to idle for retry
      transitions from: :running, to: :idle, 
                  unless: :exhausted_retries?,
                  after: :increment_retry!
    end

    event :reset do
      transitions from: [:completed, :failed], to: :idle
    end
  end

  def initialize
    @retry_count = 0
    @error_log = []
  end

  private

  def exhausted_retries?
    @retry_count >= 2
  end

  def increment_retry!
    @retry_count += 1
  end

  def log_failure(reason = "Unspecified error")
    @error_log << "[#{Time.now}] Failed: #{reason}"
  end
end

#

puts "=== 1. Initial State Initialization ==="
runner = TaskRunner.new
puts "Initial State: #{runner.aasm.current_state}" # => :idle
puts "Is idle?       #{runner.idle?}"             # => true
puts "Can run?       #{runner.may_run?}"          # => true
puts "Can complete?  #{runner.may_complete?}"     # => false

puts "\n=== 2. Attempt 1: Transient Failure (Retry loop) ==="
runner.run!
puts "State after run!: #{runner.aasm.current_state}" # => :running
runner.fail!("Connection Timeout")
puts "State after fail 1: #{runner.aasm.current_state}" # => :idle (fallback triggered)
puts "Retry Count:        #{runner.retry_count}"        # => 1

puts "\n=== 3. Attempt 2: Second Transient Failure ==="
runner.run!
runner.fail!("503 Service Unavailable")
puts "State after fail 2: #{runner.aasm.current_state}" # => :idle (fallback triggered)
puts "Retry Count:        #{runner.retry_count}"        # => 2

puts "\n=== 4. Attempt 3: Retries Exhausted (Permanent Failure) ==="
runner.run!
puts "Exhausted guard will evaluate to: #{runner.send(:exhausted_retries?)}" # => true
runner.fail!("Database Unreachable")
puts "Final State: #{runner.aasm.current_state}" # => :failed
puts "Is failed?   #{runner.failed?}"            # => true
puts "Error Log:"
puts runner.error_log

puts "\n=== 5. State Recovery (Reset) ==="
puts "Can reset?   #{runner.may_reset?}"          # => true
runner.reset!
puts "State after reset!: #{runner.aasm.current_state}" # => :idle

puts "\n=== 6. Invalid Transition Guardrail ==="
begin
  # Attempting to complete a task that is currently idle (invalid transition)
  runner.complete!
rescue AASM::InvalidTransition => e
  puts "Caught expected exception: #{e.message}"
end
