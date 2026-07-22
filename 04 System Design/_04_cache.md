Architectural Flow
 
In this diagram, the green directional arrows show the exact flow of network communication:
    1. Clients / Devices -> Request data via the Cloud / Internet.

    2. Load Balancer -> Distributes incoming traffic evenly across app servers.

    3. Application Servers (the 5 server icons) -> Execute business logic and send data requests downwards.

    4. Caching Layer -> Intercepts calls from application servers.
        Cache Hit: If data exists in cache, it immediately returns the payload to the application server (bypassing DB completely).
        Cache Miss: The Caching Layer (or application server, depending on implementation strategy) queries the Database directly.

	5. Database (DB) -> Persists master data and returns results up through the cache layer to the requesting application server.

3 Patterns

    1. Cache-Aside (Lazy loading)
        Application reads/write to both cache layer and database directly. Database knows nothing about Cache. Cache knows nothing about database.

        Flow Mechanics
            Read Path:
                1. Application checks the Cache for the requested key.
                2. Cache Hit: Cache returns data immediately to the application.
                3. Cache Miss: Cache returns null. Application queries the Database directly.
                4. Application writes the returned data into the Cache with a Time-To-Live (TTL) for future requests.
            Write Path:
                1.	Application writes/updates data directly in the Database.
                2.	Application invalidates (deletes) or updates the corresponding key in the Cache.

        Pros & Cons
            Pros: Resilient to cache failure (if cache crashes, application falls back to DB); highly customizable; memory-efficient because only explicitly requested data is cached.
            Cons: Potential for cache stampedes/thundering herd on cache misses; risk of stale data if DB updates fail to invalidate cache.

    2. Read-Through
        In this pattern, the application treats the caching layer as the main data store. The application never queries the database directly for reads. Instead, the cache layer acts as an inline proxy that fetches missing data from the DB behind the scenes.

        Flow Mechanics
            Read Path:
                1. Application requests data from the Caching Layer.
                2. If data is present (Hit), Cache returns it to Application.
                3. If data is missing (Miss), the Cache plugin/middleware reads from the Database, populates its internal cache store, and returns the result back to the Application.
            Write Path:
                Writes are handled separately (usually combined with Write-Through (2.1) or Write-Around (2.3)).

        Pros & Cons
        	Pros: Simplifies application code (application logic only handles single interface/data provider); seamless caching abstraction.
        	Cons: Requires specialized caching libraries or plugins capable of DB connectivity; initial reads for new data still incur latency.

    2.1 Write-Through
        Write-Through ensures synchronous data consistency between the Cache and the Database. Whenever data is written or updated, it is written to the cache and database simultaneously before returning a success response to the caller.

        Pros & Cons
            Pros: High data consistency; zero risk of stale cache reads; newly updated data is immediately warm in RAM.
            Cons: Higher write latency because every write must complete across two systems (Cache + DB) before returning to the user; cache bloat if written data is rarely read.

    2.2 Write-Behind (Write-Back)
        Write-Behind prioritizes write performance. The application writes directly to the cache, which acknowledges success immediately. The cache layer then queues and asynchronously persists the changes to the database in the background.

        Flow Mechanics
            Write Path:
	            1.	Application writes data to the Cache Layer.
	            2.	Cache Layer updates memory and instantly returns 200 OK to the application.
	            3.	Background process/daemon in the Cache Layer batches multiple queued writes together.
	            4.	Cache Layer asynchronously bulk-inserts/updates the Database.

        Pros & Cons
            Pros: Minimal write latency (data is written only to cache initially); improved user experience for write-heavy operations.
            Cons: Risk of data loss if the cache fails before data is persisted to the database; increased cache complexity for managing write-backs.

    2.3 Write-Around
        Write-Around bypasses the caching layer completely during write operations. Data is written directly to the primary database, leaving the cache unmodified.

        Pros & Cons
            Pros: Prevents cache pollution/flooding with data that is written once and rarely read again (e.g., real-time audit logs or user uploads).
            Cons: Higher read latency for newly written data on its first read request ("cold start").

--------------------------------------

## Summary

We have 2 main patterns

    Application -> Cache
                -> Database

    Application -> Cache -> Database
                
Some strategies between read and write

    1.  Application reads cache, if missed, application reads database and writes to cache (Cache-Aside)
        Application writes to database, and deletes cache. (Write-Around)
    
    2.  Application reads cache, if missed, cache reads database and writes to cache (Read-Through)
        Application writes to Cache
            Cache synchronously writes to database and return successful response for Application (Write-Through)
            Cache writes to database asynchronously via external middleware (message queue, or background process). (Write-Behind)
