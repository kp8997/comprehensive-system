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
        
Layers of ***Non Blocking I/O***
