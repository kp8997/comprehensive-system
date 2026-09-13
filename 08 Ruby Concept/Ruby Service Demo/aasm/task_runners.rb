require 'aasm'

class TaskRunner
  include AASM

  attr_accessor :retry_count, :error_log

  aasm do
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
