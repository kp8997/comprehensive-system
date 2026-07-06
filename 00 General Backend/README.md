System Taxonomy: Backend Architecture & Java Mastery
 │
 ├── 1. Language Mechanics (Java Deep Dive)
        - HashMap Collisions: What happens internally when two distinct keys produce the same hash code in Java 8+? Expectation: Explain the transition from a LinkedList to a Balanced Tree (Red-Black Tree) when the bucket threshold crosses TREEIFY_THRESHOLD = 8.
        - Thread Safety Mechanics: Differentiate between HashMap, Collections.synchronizedMap(), and ConcurrentHashMap. How does ConcurrentHashMap achieve high concurrency without locking the entire map?
        - Immutable Classes: How do you design a custom, deeply immutable class in Java? Why are immutable objects preferred as keys in a Map?
        - Virtual Threads (Project Loom): How do Virtual Threads in Java 21+ alter the traditional 1:1 mapping of Java threads to OS threads? Explain how they handle blocking I/O calls without exhausting system resources.

 ├── 2. Framework & Runtime Engine (Spring Boot & JVM)
 ├── 3. Data Topology & Persistence (SQL, NoSQL, Caching)
 └── 4. Distributed Systems & Scale Architecture (Microservices)
