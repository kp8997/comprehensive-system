3. System Design & Architectural Resilience:

The second image highlights the core philosophy interviewers look for: "Build systems that can fail, not just systems that can run."

Idempotency & Distributed Consistency:
  
  "How do you guarantee strict idempotency in gamified point distribution or rewards (e.g., preventing duplicate points when a mobile client retries an interrupted network request)?"
  
  "How would you architect a distributed job queue using BullMQ/Redis or GCP Pub/Sub to handle workers crashing halfway through processing without losing messages?"
  Fault Tolerance & Availability:
  
  "What failover patterns (Circuit Breaker, Exponential Backoff with Jitter, Dead-Letter Queues) do you apply when an external service or database goes down?"
  
  "How do you handle database failover and replica lag when orchestrating writes to primary nodes and reads from read-replicas?"

Mobile-Specific API Design:

  "How do you optimize mobile payload sizes and manage schema versioning across multiple legacy client versions in production?"
