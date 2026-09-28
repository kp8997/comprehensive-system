Container Orchestration

Resilience

  Mistake: Want the node run the whole time
  Core concept: sometimes, we want app to be crashed to identify earlier mismatch, some times we want to keep the app running and handle gracefully. The purpose is keep the system state even though the death of some arbitrary processes

  Topic:
    1. Death of a nodejs process: No need to catch those fatal event just let it dead, or cache with right handle before restart it

      ```js
        process.on('uncaughtException', (err) => { ... });[span_5](start_span)[span_5](end_span)
        process.on('unhandledRejection', (reason, promise) => { ... });[span_6](start_span)[span_6](end_span)
      ```
      instead: log full context and stack trace to stdout
        No taking new traffic
        Gracefully finish in-flight request
        Call process.exit(1) and let the container (k8s/docker) auto restart instance

    2. Build stateless service and apply single source of truth
      Eliminate Hidden Local State

      Shift Responsibility on Failure: surface a structured 5xx error back to the client early, pushing the responsibility of retrying the state modification back to the caller.

      Offload to External Brokers: To survive sudden process death, all distributed transaction state must be offloaded to external message brokers or event logs

    3. Memory - Unbounded memory and bounded caching - cache is disposable - should be clean up
      unbounded cache: Using Map or memory structure too much without eviction cause Out of Memory event, can harm V8
      
      bounded cache: using LRU to evict old record in memory / redis instance

      Cache Wipe on Restart simultaneously - Cache Stampede / Thundering Herd: If all the cache expired the same time, the requests now navigate to DB directly can harm the stateful DB

    4. Database connection resilience: We can leverage /health that external services will track this and navigate requests to other active instance, at the same time and will restart failure instance 

      drivers ioredis can support automatic reconnection, other DB like postgreSQL may require explicit

      Know when to return 200 degraded or 503 service unavailable to handle error properly. 5xx early error, 200 degraded slow but okay to handle

    5. Survive network failure - Exponential Backoff: If we have retry mechanism, we need to implement exponential backoff to avoid thundering herd on service that can cause the service down immediately.

    Exponential Backoff: use retry with 2n time: 50ms 100ms 200ms

    Add random factor of retry time / request: const retryTime = Math.random() * (baseTime * 0.2) + baseTime * 0.9;

    The Circuit Breaker Pattern: to avoid flooding requests to struggling service,
      3 state
      - closed: normal
        When request fail at first
      - open: after X request fail, we will open the circuit and return error immediately
      - half-open: after Y seconds, we will let some request pass through to check if the service is recovered. If not recover back to open, if yes back to closed.

    6. Chaos Engineering & Resilience Testing: Senior engineers do not rely solely on the "happy path". Simulated Chaos Boundaries:

      Random Crashes: Simulating an abrupt process.exit() to verify that the orchestrator (Kubernetes) brings up a new pod and the client-side retries succeed without data loss.

      Injected Network Latency / Blips: Adding random artificial latency or rejected promises to database calls to test timeouts and circuit breakers

      Tooling: Tools like Netflix's Chaos Monkey test infrastructure resilience by randomly terminating container instances across the cluster.

Distributed Primitives

Security
