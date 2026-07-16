1 CPU processor (CPU core) process a thread at a time. Many threads wait at RunQueue to be executed

1 CPU have their own L1/L2 cache to store value of those threads , and share L3 cache with other CPU cores

Platform threads (traditional threads):
Context switch is a process to save thread's context (cache CPU , stack value) to RAM, and load another thread's context to CPU. This is expensive process

    1. CPU halts Java code and transition from User Mode to privileged Kernel Mode.

    2. OS kernel save everything in CPU cache (16+ general-purpose registers, floating-point math registers, vector registers, and internal CPU state registers, etc) to RAM.

    3. OS Scheduler decide what is the next thread to process for that (a) processor core.

    4. OS switch back from Kernel Mode to User Mode, loads another thread's context into CPU cache and let it continue its execution.

Cost: This boundary crossing is incredibly slow. A platform thread context switch typically takes 1,000 to 10,000 nanoseconds (about 1 to 10 microseconds).

Project Loom - Virtual Threads:
The OS sees a single, continuously running platform thread (the "Carrier Thread") that never stops. Under the hood, the Java Virtual Machine (JVM) acts as its own mini-operating system completely within User Space.

    1.	No Boundary Crossing: The CPU stays in User Mode. No expensive OS kernel trap is triggered.

	2.	Just Objects in Memory: The "context" of a Virtual Thread is just a standard Java object living on the JVM Heap.

	3.	Heap Swap: The JVM simply copies the current call stack of Virtual Thread A onto the Heap, and copies the saved call stack of Virtual Thread B from the Heap back into the Carrier Thread's execution frame.
    
	4.	Resume: The CPU carries on executing instructions.

Stack vs Heap Memory

    Heap: Physical RAM; Memory uses for storing objects. Global memory shared by all threads. Any object by `new` keyword allocates in heap.

    Stack: CPU Registers + RAM; CPU uses temporary data, execution state: variables of method (in scope), method frame, stack trace, thread's context

Volatile: designed for Single-Writer, Multiple-Reader scenarios.

------------------------------------------
Stack vs Heap for thread:

Heap (size fixed for JVM):
    Class metadata
    Loaded classes
    Static variables
    Instance variables of all objects
    Array elements
    Method arguments (references)
    Dynamically allocated objects

Stack (size fixed per thread):
    Local variables (value type primitives, object references)
    Method arguments (values)
    Method frame (return address, local variables, operand stack)
    Execution context (program counter, register state)

------------------------------------------
What is a Thread Pool?

A Thread Pool is a managed collection of worker threads that sit ready to execute tasks submitted to a queue. Instead of the classic, expensive pattern of:

Request $\longrightarrow$ Create Thread $\longrightarrow$ Execute $\longrightarrow$ Destroy Thread


 [ EXECUTOR SERVICE ARCHITECTURE ]
                  
   Tasks Submitted
   (Runnables/Callables)
        │
        ▼
   ┌──────────────────────────────────────────────┐
   │ TASK QUEUE (LinkedBlockingQueue)              │
   │  [Task 4]  [Task 3]  [Task 2]  [Task 1]      │
   └──────────────────────┬───────────────────────┘
                          │ (Assigns task when thread is free)
                          ▼
   ┌──────────────────────────────────────────────┐
   │ THREAD POOL                                  │
   │  ┌──────────┐   ┌──────────┐   ┌──────────┐  │
   │  │ Thread-1 │   │ Thread-2 │   │ Thread-3 │  │ (Reused continuously)
   │  │  (Idle)  │   │  (Busy)  │   │  (Busy)  │  │
   │  └──────────┘   └──────────┘   └──────────┘  │
   └──────────────────────────────────────────────┘

=============================
### How to Choose the Right Strategy for Maximum Performance

Thread is expensive because it has own stack (1MB) and it need to allocate RAM for stack, CPU cache...

    Use thread for heavy computational tasks

    Use thread pool for managing threads, to avoid overhead looping for every task requires thread
