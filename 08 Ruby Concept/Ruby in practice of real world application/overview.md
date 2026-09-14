This is real application lesson learn from my previous several companies that i remembered and note in this folder.

The structure of this folder may focus on the vital factor first and can help leverage when answer brief questions from interviewer.

List items of:

  gRPC: client server connect, .proto (with shape of request, response and method name ) => generate code needed to call. client call function with payload, callback. server call function with context (request), callback in this case.

  Redis: cached big repeated data in memory

  SideKiq: Concurrency handle for heavy process

  PostgreSQL
    Transaction
      Retry:
    Index: Columns
    Explain: To optimize logic
    View & Materialized View: View is saved on RAM, Materialized View is save on non-volatile (Disk)

  ActiveRecord
    Validation:
    Callback (Observable):

  Race Condition Handler:

  aasm: to constraint between states, and function to transition.
    Example:
      state_machine :initial => :open do
        state :accepting, :delivering, :closed

        event :accept do
          transitions :from => :open, :to => :accepting
        end

        event :deliver do
          transitions :from => :accepting, :to => :delivering
        end

        event :close do
          transitions :from => [:open, :accepting, :delivering], :to => :closed
        end
      end
