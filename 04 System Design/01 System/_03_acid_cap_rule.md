ACID compliance rule: would apply for data universal. It is especially tailored for ***transaction***

    A: Atomicity
        All or nothing, a transaction must be successful or nothing executed
    C: Consistency
        All data must be valid, they must satisfy all the rules and constraints across databases, shards, replicas
    I: Isolation
        Concurrent execution of transactions via isolated sessions. Not any session affect another's data
    D: Durability
        All data must be saved. When the transation finish, the changes will permanently apply into database.

Breaking Down the ACID Components
To make this abstract concept easily scannable and memorable, let's break down the four pillars of ACID like an engineering manual.

1. Atomicity ("All or Nothing")
    Concept: A transaction often consists of multiple smaller statements. Atomicity ensures that the database treats the entire series of operations as a single, indivisible unit.
    Mechanism: If every single statement succeeds, the transaction is committed. If even a single statement fails, the entire transaction is aborted, and the database is rolled back to its exact state before the transaction began.
    Manual Rule: Never allow a partial state.

2. Consistency ("Rules Are Rules")
    Concept: A transaction can only take the database from one valid state to another valid state, maintaining all predefined schema rules, constraints, and cascades.
    Mechanism: Before and after any transaction, data must adhere to all database invariants (e.g., unique keys, foreign key constraints, or custom checks like Balance >= 0). If a transaction attempts to violate these rules, the database rejects it.
    Manual Rule: Protect the integrity of data structures at all costs.

3. Isolation ("Do Not Disturb")
    Concept: Databases handle thousands of transactions concurrently. Isolation ensures that the concurrent execution of transactions leaves the database in the same state as if they were executed sequentially (one after another).
    Mechanism: It hides uncommitted changes of one transaction from other concurrently running transactions using locking mechanisms or Multi-Version Concurrency Control (MVCC). There are different Isolation Levels (e.g., Read Committed, Serializable) that balance performance against absolute isolation.
    Manual Rule: Prevent transactions from stepping on each other's toes.

4. Durability ("Built to Last")
    Concept: Once a transaction is successfully committed, its changes are permanently written to non-volatile storage (like a hard drive or SSD).
    Mechanism: Even if the database server abruptly loses power or crashes one millisecond after a commit, the data will not be lost. Databases achieve this using a Write-Ahead Log (WAL), recording changes to disk before updating the actual database blocks.
    Manual Rule: System crashes must never cause data regression.

CAP Theorem: The idea of it is: we can only have 2 of them, but not 3

    C: Consistency. It is different about the term in ACID, here is about write data and can read it back immediately. Not read the stale data
    A: Availability. It is mean that the system is always available and can respond to requests
    P: Partition tolerance. It is mean that the system can tolerate network partitions and still function properly

***Note***

Consistency in ACID and CAP is different:
    In ACID, consistency is about the integrity of data structures and business rules, ensuring data remains valid before and after a transaction.
    In CAP, consistency is about the integrity of data across the system, and that the data is available to all nodes in the system.

  [A] Availability
                     / \
                    /   \
                   /     \
                  /  (AP) \
                 /         \
                /  Cassandra\
               /             \
              /               \
   MySQL     /_________________\  (CP)  Apache HBase
   PostgreSQL                           MongoDB
     (CA)                               DynamoDB (Strongly Consistent)
[C] Consistency                     [P] Partition-Tolerance

*** In terms of design system ***

CAP for macro level: system level - software architect choice
ACID for micro level: transaction level - backend developer/data engineer choice. Decide what logic should be bounded in transaction to ensure the correctness of business data

    
