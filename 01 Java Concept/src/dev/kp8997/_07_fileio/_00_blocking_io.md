We have 3 layers of ***Blocking I/O*** to easily understand:
   
1. The Ground Layer:
	DataSource (byte) $\longrightarrow$ FileInputStream $\longrightarrow$ Java program that read byte
	What it is: A raw byte-oriented input stream.
	How it works: It communicates directly with the Operating System to read physical, raw binary data (8-bit bytes) from a file. It has no concept of encoding, letters, or words; it only sees numeric byte values from 0 to 255.


2. The Translator Layer:
	DataSource (byte) $\longrightarrow$ FileInputStream + InputStreamReader $\longrightarrow$ Java program that read characters
	What it is: A character-oriented bridge stream.
	How it works: It acts as a converter. It intercepts the raw binary bytes from the FileInputStream and decodes them into Unicode characters (16-bit Java char types) using a specified character set (like UTF-8).
	The Drawback: It still reads data sequentially, often triggering a system call to read from the disk for every single character or small array chunk requested.


3. The Performance Layer:
	DataSource (byte) $\longrightarrow$ FileInputStream + InputStreamReader $\longrightarrow$ BufferedReader $\longrightarrow$ Java program that read characters
	What it is: A buffered character stream.
	How it works: It wraps the InputStreamReader and introduces a default $8192$ character cache buffer (an internal char[] array). Instead of hitting the disk for every character, it pre-fetches a large block of text into RAM, serving subsequent read operations instantly.

Whenever JVM execute the read() or readLine() function, it will trigger the process above to move data from file to heap memory.

How does data move through this system when you call reader.readLine() or reader.read()?

    [ JVM App ]        [ OS Kernel ]       [ Page Table ]       [ Disk Controller ]      [ RAM (Page Cache) ]
     |                  |                     |                      |                       |
     |-- 1. Read() ---->|                     |                      |                       |
     |                  |-- 2. Lookup Page -->|                      |                       |
     |                  |<-- 3. Cache Miss ---|                      |                       |
     |                  |                                            |                       |
     |                  |-- 4. Send DMA Command (Read Block) ------->|                       |
     |                  |                                            |-- 5. Copy data ------>|
     |                  | <================= CPU is free to do other work =================> |
     |                  |<-- 6. Trigger Hardware Interrupt ----------|                       |
     |                  |                                                                    |
     |                  |-- 7. Update Page Table (Cache Hit Now!) -------------------------->|
     |<-- 8. Return ----|                                                                    |
        
First it execute the read(), then lookup Page Cache table in RAM:
	if is empty, OS sends the DMA command to controller block to copy data from disk to RAM (a block of disk usually has 4KB of sector), when copying data, current thread is sleeping and OS will do a context switch for another thread to execute, when copying process is finished, OS trigger interrupt to wake up the current thread, then OS copies data from RAM to heap memory of Java application, then return back to CPU (free thread).
	If have data, OS copies data from RAM to heap memory of Java application, then return back to CPU (free thread).

More information about DMA: When DMA make the controller copy 4Kb to Page Cache, it can automaticall fetch more data due to pre-fetch algorithm of the OS when OS can detect sequentially reading data.

Whenever JVM executes the read(), it will move data from Page Cache to Heap Memory (where object stored). This require the switch mode (from user to kernel mode and vice versa again to return the cpu for JVM), this is expensive process.

switch mode is transition of architecture privilege level
	user mode is low privilege, kernel mode is high privilege
	user mode is JVM, application logic, and some of OS API, kernel mode is OS kernel, hardware.

When we read a 16 KB file
	if we use read() with no buffer,
		we have 16384 times of switch mode, because OS copy data 1 byte at a time.
		we have 4 context switching due to 4 times of DMA command.
	if we use read() with default BufferedReader (size 8kb)
		we have 2 times of switch mode, because OS copy data 8kb at a time.
		we have 4 context switching due to 4 times of DMA command. (but lower number = 1 or 2 if pre-fetch happen)
	if we use read() with buffer (size 16kb)
		we have 1 time of switch mode, because OS copy data 16kb at a time.
		we have 4 context switching due to 4 times of DMA command.

The size of buffer:
	should be power of 2kb because the sector size when copying is 4kb.
	Should be between 4KB to 32KB.

If the size is more than that:
	1. L1/L2 cache are about ~32kb/512kb, when process in Stack memory, it can not fit in the cache, so it will take more time to access by pulling it from RAM (RAM latency is about ~100ns, CPU clock is about ~0.3ns).
	(Note: L3 cache (LLC) is much larger (e.g., 8MB–64MB) and is shared by all cores. For very large buffers, you might see performance improve again if the data fits in L3, but you will definitely suffer from the increased GC overhead mentioned below.)
	2. the time save for switch mode and context switching is swallowed up by the overhead of allocate memory on heap.
	3. garbage collection has to work more to wipe those large buffer, can cause the freeze of thread
