Concurrency & Thread
We have some layers like NodeJS native API -> Node JS binding to C -> V8 | Event loop (libuv) | OpenSSL | Zip

V8 - Stack: Handle line by line, open stack frame for function calling
Event loop - Libuv: handle non-blocking requests: (6 phases for 6 types of requests)

Chunking Heavy:
	Original: it will synchronously run the line of heavy code, block micro and macro tasks
	Solution: use setImmediate() in the synchronous code, that will enter event loop taks
	Note: not use process.nextTick() because it can behave the same, only run synchronous and promise code, no event loop entering

Scaling beyond a process
	create child processes from parent by fork(): 2 ways of communication: 
		1. is via parent listener event (as broker message), 
		2. is peer to peer connection via IPC (socket)
	2 ways of fork(): 
		1. child_process.fork(‘file_path’): run a new process that run independently, not allow network port collision
		2. cluster.fork(): re-execute the current code instance on server process run different core

Protocol & Inter-Service Communication

2 Main ways of communication
	Synchronous - request, response : Rest, gRPC, GraphQL
	Asynchronous- event, queue-driven: redis, kafkaesque, rabbitMQ

HTTP/Rest Trait
	Status code type
	Idempotency: GET, PUT, DELETE are idempotency (the same result as requested multiple times)
	Non-Idempotency: POST (not the same result as request the same data - create data per request)

POJO 
	problem: by default, simple serialization with JSON.stringify() => cause security issue
	solution: use DTOs or define toJSON() (of class of object) to only publish public fields

HTTP Compression: should not use because overhead of encrypt and decrypt outweigh the size matter

GraphQL: retrieve a single aspect of data instead of all data from API,
	Vulnerability: nested loop data => exhaust CPU and database pool (author -> books -> author -> books …)
	Solution: limit depth of nested

gRPC: send in .proto file that is binary. Use for internal services communication, can be local (IPC with socket) or remote connection (TCP with HTTP/2)
	Benefit: size is small, fast,
	Note: in other languages, gRPC faster, but in NodeJS HTTP may be faster because
		Protobuf is binary, use nodeJS Buffer, which is executed in javascript engine and C++
		JSON stringify/parse use C++ totally under the hood, so It is faster

Cluster module, Reversed Proxy with HAProxy, Load Testing with Autocannon

Cluster is an anti-pattern in distributed system. 
	It can not scale with horizontal servers because it bases on clone the server instance that run on multiple core of CPU.
	If machine only has 1 core, config it with multiple instances running will degrade performance
	it can cause other worker starvation mean while a worker is overuse with massive number of requests with gRPC. 
		Base on gRPC protocol (HTTP/2 over TCP) the TCP connection will keep opening, and http request of gRPC keep coming, 			meanwhile old protocol with HTTP/1 will close TCP connection after http request handled.
		Cluster master process handle only TCP handshake and pass to the worker, it can not inspect the HTTP request inside

Reversed Proxy: 
	We should use it for NodeJS to offload unnecessary services like HTTPS certificate, health check, security sanitization. Let Nodejs 	focus to handle business request only
	HAProxy can handle back pressure with maxconn, if too much requests, it will queue (instead of drop out - nodejs http server’s default behavior when reach maxcon)

Load Testing:
	Criteria: SLA (server uptime - e.g 4 nines), SLO (for api with response time and uptime), 
	Benchmark with Autocannon: use for SLO to test threshold of API
