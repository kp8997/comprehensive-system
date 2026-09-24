1. AI Basics & LLM in Production
Because Elfie specifically lists Google Cloud Platform (GCP), Vertex AI, and OpenAI for a consumer health app, expect questions about integration, data security, and API resilience rather than low-level model training.

LLM Fundamentals & Architecture:
  "How do you differentiate between fine-tuning, embeddings for RAG (Retrieval-Augmented Generation), and prompt chaining for backend workflows?"

  "How do you ensure deterministic JSON outputs from non-deterministic LLMs when orchestrating downstream backend services?"

Production Engineering (Vertex AI / OpenAI):
  "How do you handle severe rate-limiting (429 Too Many Requests), latency spikes (TTFT - Time to First Token), and token context window limits in high-throughput backend services?"

  "How do you safeguard user PII (Personally Identifiable Information) or sensitive medical health data before transmitting prompts to 3rd-party LLM providers?"

  "What is your strategy for semantic caching (e.g., using Redis Vector Search) to reduce external API costs and latency?"

2. Computer Science Core (Data Structures, Algorithms, OS, & Networks)
At a senior level, these questions focus on computational complexity and runtime trade-offs.

Data Structures & Algorithms:

  "How would you implement a sliding-window counter or a token-bucket algorithm in memory vs. across distributed nodes for an API rate limiter?"

  "When would you pick a Min-Heap or B-Tree over a Hash Table for scheduling tasks or query indexing?"
  Operating Systems & Node.js Runtime:

  "Explain the Node.js Event Loop phases (timers, pending callbacks, poll, check, close). Where do process.nextTick() and setImmediate() execute?"

  "How does the Node.js V8 memory model manage the heap and stack? How do you diagnose and resolve memory leaks using heap snapshots?"

  "How does Node.js leverage libuv worker threads, and what happens to the event loop if CPU-bound cryptographic operations or regex execution block the main thread?"

Networking & Protocols:

  "Explain the TCP 3-way handshake and head-of-line blocking. Why migrate from HTTP/1.1 to HTTP/2 or HTTP/3 (QUIC) for mobile app backend communication?"

  "What mechanisms prevent TCP socket exhaustion in a high-concurrency Node.js API gateway making upstream microservice calls?"

3. System Design & Architectural Resilience
The second image highlights the core philosophy interviewers look for: "Build systems that can fail, not just systems that can run."

Idempotency & Distributed Consistency:
  
  "How do you guarantee strict idempotency in gamified point distribution or rewards (e.g., preventing duplicate points when a mobile client retries an interrupted network request)?"
  
  "How would you architect a distributed job queue using BullMQ/Redis or GCP Pub/Sub to handle workers crashing halfway through processing without losing messages?"
  Fault Tolerance & Availability:
  
  "What failover patterns (Circuit Breaker, Exponential Backoff with Jitter, Dead-Letter Queues) do you apply when an external service or database goes down?"
  
  "How do you handle database failover and replica lag when orchestrating writes to primary nodes and reads from read-replicas?"

Mobile-Specific API Design:

  "How do you optimize mobile payload sizes and manage schema versioning across multiple legacy client versions in production?"

4. Node.js & Python Ecosystem Experience
Elfie requires both Python and Node.js for their microservices backend.

Node.js (Core Runtime):
  "How do you handle stream pipelines and backpressure when streaming large files or continuous data streams without exceeding RAM boundaries?"

  "What is your preferred clustering and process management strategy in containerized Docker/GCP environments (e.g., single process per container vs. Node cluster module)?"

Python Interoperability & Tooling:
  "Python uses a Global Interpreter Lock (GIL) and an asyncio event loop. How does Python's concurrency model contrast with Node.js event-driven architecture?"

  "How do you structure microservice boundaries when Python is used for AI/data tasks and Node.js serves customer-facing REST/gRPC endpoints?"

Databases & DevOps (GCP & NoSQL):
  "What data modeling strategies do you apply in NoSQL (e.g., MongoDB, DynamoDB, Firestore) for one-to-many relationships: embedding vs. referencing?"

  "How do you construct CI/CD pipelines, optimize multi-stage Docker builds, and configure health/readiness probes on Kubernetes/Cloud Run?"

5. Problem-Solving & Technical Troubleshooting

Expect scenario-based production triage questions:

  "Your Node.js service suddenly reports CPU usage spiking to 100% and response latency increasing by 500ms. Walk through your step-by-step triage and diagnostic plan."

  "A batch of reward notifications sent identical duplicate push messages to thousands of mobile users. How do you find the root cause, mitigate the immediate issue, and ensure it cannot recur?"

6. Communication & Leadership

"How do you handle trade-offs between code perfection and rapid startup iteration when deadlines are tight?"

"Describe a time you delivered critical feedback during a pull request review to an engineer who strongly disagreed with your architectural recommendation."
