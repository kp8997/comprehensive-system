1 CPU processor (CPU core) process a thread at a time. Many threads wait at RunQueue to be executed

1 CPU have their own L1/L2 cache to store value of those threads , and share L3 cache with other CPU cores

context switch is a process to save thread's context (cache CPU , stack value) to RAM, and load another thread's context to CPU. This is expensive process
