2. Computer Science Core (Data Structures, Algorithms, OS, & Networks)
At a senior level, these questions focus on computational complexity and runtime trade-offs.

Data Structures & Algorithms:

  "How would you implement a sliding-window counter or a token-bucket algorithm in memory vs. across distributed nodes for an API rate limiter?"

    => in memory so we have single node. We can use Map in javascript to store data

    => distributed nodes so we have multiple instance - nodes. Use redis with lua script to leverage atomic update. Multiple node retrieve redis to get data about rate limiter

    => token-bucket and sliding window usage are to make sure the number of requests per minute must be uniformly distributed

  "When would you pick a Min-Heap or B-Tree over a Hash Table for scheduling tasks or query indexing?"

Operating Systems & Node.js Runtime:

  "Explain the Node.js Event Loop phases (timers, pending callbacks, poll, check, close). Where do process.nextTick() and setImmediate() execute?"

  "How does the Node.js V8 memory model manage the heap and stack? How do you diagnose and resolve memory leaks using heap snapshots?"

  "How does Node.js leverage libuv worker threads, and what happens to the event loop if CPU-bound cryptographic operations or regex execution block the main thread?"

Networking & Protocols:

  "Explain the TCP 3-way handshake and head-of-line blocking. Why migrate from HTTP/1.1 to HTTP/2 or HTTP/3 (QUIC) for mobile app backend communication?"

  "What mechanisms prevent TCP socket exhaustion in a high-concurrency Node.js API gateway making upstream microservice calls?"
