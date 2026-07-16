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
    
    memory visibility issue: a thread updates the value of a variable, but other threads do not see the updated value.
    thread pinning (for project loom only): thread is pinned to a specific CPU core, no matter what happen to the CPU, the thread will always run on that core

3. Orchestrators & How to Manage Them

    Low-Level Guardrails (Memory Barrier & Locks)
    
        volatile: force read/write to main memory, prevent thread caching data, prevent reorder instruction. designed for Single-Writer, Multiple-Reader scenarios. Multiple write can cause

        synchronized: Ensures that a block of code (not just a single variable) is executed by only one thread at a time.

            intrinsic lock (monitor) on an object, ensures mutual exclusion (only one thread executes at a time) and memory visibility;
                => Implicit lock/unlock

            ReentrantLock: explicit lock offering more advanced features than synchronized, like fairness, trylock (non-blocking attempt), interruptible locking, and condition variables.
                => Explicit lock/unlock (unlock must be in finally block)

        atomic operation: use cpu hardware instruction to perform operation on a single variable. example: AtomicInteger, AtomicLong, AtomicReference.

    High-Level Thread Pools (ExecutorService)

        Thread pool is a cache of threads, used to execute tasks

    Structured Concurrency:

        Treats multiple tasks running on different threads as a single unit of work. If one subtask fails, all other subtasks are automatically canceled, preventing thread leaks.

=============================
### The 4 Fatal Pitfalls of Monitor Locks

The Constant Pool Trap (Locking on Strings/Boxed Primitives)

    // DO NOT DO THIS!
    public void process() {
        synchronized ("my_shared_lock") { // CRITICAL BUG!
            // ...
        }
    }

    Why it crashes your system: If another developer in a completely different part of your project (or even a third-party library) also synchronizes on "my_shared_lock", both unrelated parts of the system will block each other! You have inadvertently created a global lock.

Locking on Non-Final/Mutable Fields (The Moving Target)

    // DO NOT DO THIS!
    private Object lock = new Object(); // Missing 'final'!

    public void update() {
        synchronized (lock) {
            // ...
            lock = new Object(); // The reference changes!
        }
    }

    Why it crashes your system:
        1.	Thread A enters the block using the first lock object address (0x001).
        2.	While inside, Thread A reassigns lock to point to a new object address (0x002).
        3.	Thread B arrives, checks lock (which is now pointing to 0x002), sees that it is free, and enters the critical section simultaneously!
        4.	Result: Mutual exclusion is completely broken, leading to silent data corruption.

Deadlock (The Inextricable Wait)

    // Thread A execution path:
    synchronized (Lock1) {
        synchronized (Lock2) { ... }
    }

    // Thread B execution path:
    synchronized (Lock2) {
        synchronized (Lock1) { ... }
    }

    Why it crashes your system: If Thread A grabs Lock1 at the exact same moment Thread B grabs Lock2, both threads will pause indefinitely waiting for the other to release. Because they are blocked, they can never reach the end of their blocks to release their own locks.

The "Spurious Wakeup" and if Statement Trap

    // DO NOT DO THIS!
    synchronized (lock) {
        if (!jobQueue.isEmpty()) { // BUG! Should be a 'while' loop
            lock.wait();
        }
        processJob(); // Can throw NullPointerException if queue is actually empty!
    }
    
    Why it crashes your system: Operating systems can occasionally wake up a sleeping thread without any explicit notify() call being sent (called a Spurious Wakeup). If Thread A wakes up and the check was a simple if statement, it will bypass the check and attempt to process a job from an empty queue, crashing with a runtime exception.
    