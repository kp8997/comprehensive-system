Step: e.g Design a real-time collaborative text editor like Google Docs
1. Functional & Non-Functional Requirements (Deconstruct the Prompt)
  functional: Text editing with presence (see who is typing)
  non-functional: Latency (sub-100ms edits), Concurrency (1000s of users), Availability (99.99%)
  estimate traffic: request per minutes

2. Resource - High architecture

  Client: HTTP/REST for standard CRUD, Websocket for real-time (typing, cursor position)

  Proxy server (API Gateway): Load Balancing, API Versioning, Auth

  Service Layer: Microservice, monolith -> choose microservice to offload heavy tasks so the main event loop still work

3. NodeJS itself - language, framework trait stuff

  Asynchronous

  worker_threads

  streaming data technologies: streams, transform streams, pipes, readable, writable, duplex, transform
  
4. Data management - Memory

  Database Selection

  Caching

  Memory handle: memory leak avoid

5. Expansion & Resilience

  Idempotency: Ensure api retry safe

  Horizontal scaling: cluster for nodejs server instance,

  Message queue: Redis, Kafka
