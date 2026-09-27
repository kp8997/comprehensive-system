Container Orchestration

Resilience

  Mistake: Want the node run the whole time
  Core concept: sometimes, we want app to be crashed to identify earlier mismatch, some times we want to keep the app running and handle gracefully. The purpose is keep the system state even though the death of some arbitrary processes

  Topic:
    1. Death of a nodejs process: No need to catch those fatal event

      ```js
        process.on('uncaughtException', (err) => { ... });[span_5](start_span)[span_5](end_span)
        process.on('unhandledRejection', (reason, promise) => { ... });[span_6](start_span)[span_6](end_span)
      ```
      instead: log full context and stack trace to stdout
        No taking new traffic
        Gracefully finish in-flight request
        Call process.exit(1) and let the container (k8s/docker) auto restart instance

    2. Build stateless service and apply single source of truth

    3. Memory - Unbounded memory and bounded caching

    4. Database connection resilience

    5. Survive network failure

    6. Chaos Engineering & Resilience Testing


Distributed Primitives

Security
