Layer architecture

    Js code

    NodeJS API (like nodejs built-in function - fs, http, etc.)

    Binding (wrapJS code with C++ wrapper and pass to libuv - it actually converts the JS parameter data into c++ data type and pass to libuv decide the next flow)

    v8 | libuv | OpenSSL | zlib

    OS

Event Loop (in libuv)
    It is designed to handle I/O-bound operations - non blocking like (Disk I/O, network I/O, CPU work)

    Event Loop has 6 main phases and will go through them in order

        timers: timers (setTimeout, setInterval)

        pending callbacks: I/O callbacks that were deferred to the next loop iteration like those callbacks for errors of (tcp, udp) or file system etc

        idle prepare: internal to Node.js to calculate the time to sleep before the next I/O operation

        poll: Disk I/O, network I/O

        check: setImmediate

        close callbacks: process.on('exit'), socket.on('close'), etc
    
    Manage the macrotask queue: timers, I/O defer, prepare phase, I/O poll, check, close callbacks
            

V8 engine
    Execute the stack, manage the memory of JavaScript, Garbage Collector (GC)

    Manage the microtask queue: process.nextTick, promises + async/await, queueMicrotask

    
Cycle:
    The V8 will call all the synchronous code first, then it will check the microtask queue and execute all the microtasks, then it will move to the next phase of the event loop.
    It will repeat this process until all the microtasks and macrotasks are executed
    
    The V8 will enter to tick queue to drain all the microtask queue before entering any phase of the event loop
    
    Example:
    tick -> timers -> ticke -> defer i/o -> tick -> idle prepare -> tick -> poll -> tick -> check -> tick -> close callback -> (next loop)

    In each phase, the event loop will check if there are any timers that are due, if there are any I/O operations that are completed, if there are any check callbacks that are due, if there are any close callbacks that are due
    
    Note: setImmediate will be executed after all the I/O operations in the poll phase are completed
