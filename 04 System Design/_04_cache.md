Architectural Flow
 
In this diagram, the green directional arrows show the exact flow of network communication:
    1. Clients / Devices -> Request data via the Cloud / Internet.

    2. Load Balancer -> Distributes incoming traffic evenly across app servers.

    3. Application Servers (the 5 server icons) -> Execute business logic and send data requests downwards.

    4. Caching Layer -> Intercepts calls from application servers.
        Cache Hit: If data exists in cache, it immediately returns the payload to the application server (bypassing DB completely).
        Cache Miss: The Caching Layer (or application server, depending on implementation strategy) queries the Database directly.
        
	5. Database (DB) -> Persists master data and returns results up through the cache layer to the requesting application server.
