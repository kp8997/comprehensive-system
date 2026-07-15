1. How thread run
    Platform Threads (The Traditional Way)
        What they are: A 1:1 mapping to Operating System (OS) threads.
        The Cost: Heavy. Each takes about 1 MB of memory for its call stack. Context switching requires OS kernel intervention.
        Rule of Thumb: Use sparingly. You must pool them (e.g., using ThreadPoolExecutor) because creating them is expensive.
    Virtual Threads (Loom)
        What they are: Lightweight, M:N user-mode threads managed entirely by the JVM, not the OS.
        The Cost: Cheap. They start with a tiny stack (~few KB) and are mounted dynamically onto platform threads (called Carrier Threads).
        The Superpower: When a virtual thread blocks on I/O (e.g., database call, HTTP request), the JVM automatically unmounts it, freeing the carrier thread to run other virtual threads.
        Rule of Thumb: Never pool virtual threads! They are ephemeral. Create one per task and throw it away.

2. Concurrency Hazards
    deadlock: block code A wait for key B, and block code B wait for key A
    race condition: two thread access the same variable (cache CPU L1, L2, L3, RAM resource), and the result is not expected
    thread pinning: thread is pinned to a specific CPU core, no matter what happen to the CPU, the thread will always run on that core

3. Orchestrators & How to Manage Them
    Low-Level Guardrails (Memory Barrier & Locks)
        volatile: force read/write to main memory, prevent thread caching data, prevent reorder instruction
        synchronized: intrinsic lock (monitor) on an object, ensures mutual exclusion (only one thread executes at a time) and memory visibility
        ReentrantLock: explicit lock offering more advanced features than synchronized, like fairness, trylock (non-blocking attempt), interruptible locking, and condition variables.
    High-Level Thread Pools (ExecutorService)
        Thread pool is a cache of threads, used to execute tasks
    Structured Concurrency:
        Treats multiple tasks running on different threads as a single unit of work. If one subtask fails, all other subtasks are automatically canceled, preventing thread leaks.
