
1. Scaling Type: Horizon vs Vertical Scaling:
    Horizon: increase horizontal scale (add more servers, nodes).
        upside:
            One down, other can take over to process upcoming requests.
            No bound, can infinitely scale.
        downside:
            Configuration overhead.
            Application must be stateless, and load balancer must be implemented.
    Vertical: upgrade capacity of server, database (RAM, CPU, Disk).
        upside:
            Just a few nodes to maintain.
        downside:
            There is a hard limit of hardware for vertical scaling. (Maximum Ram maybe e.g 1TB for a node).

2. Separate Server for application and database. There are 4 reasons:

    Resource Contention:
        Application and database server need different resources. Application server need more CPU and memory, while database server need more storage and I/O.
        Can cause bottle neck if server consumes all the CPU for app, and database doesn't have enough CPU to query data
    
    Independent Scalability:
        Application and database server have different scaling patterns.
        For example:
            application server can be scaled horizontally,
            database server can be scaled vertically.

    Fault Tolerance:
        When both database and application deployed on the same server, application crashed the OS cause the database crashed too.
        When separating, application crashed while the database still there and still do the admin job.

    Security:
        We can separate into 2 nodes, while database server only allow port e.g via 5432 for data access only.
        This can reduce the potential of attack from application layer.

3. Failover Strategies:

    Cold Standby:
        Backup server is powered off and has no current data.
        Failover requires manual intervention to power on and restore data.
        Slowest recovery time (hours to days).
        Cheapest option.
        Server is sitting in storage, no power on it, no replication.

    Warm Standby:
        Backup server is powered on but data is not actively being used.
        Data is periodically automatically synchronized (e.g., in a very small time latency).
        Faster recovery than cold standby (minutes to hours).
        Moderate cost.
        Server is sitting there and replicate data from main node in a period of time. Can not query/read to it.

    Hot Standby:
        Backup server is fully powered on and running.
        Near real-time data synchronization.
        Minimal downtime (seconds to minutes).
        Highest cost due to continuous resource usage.
        Server is sitting there and replicate data from main node in real time. Can query/read to it.

    Multi-Primary
        Application can read/write to both of Primary nodes at the same time. No rely on replication mechanics.
        Data is real time.
        When a node died and restore back, we have to resync the data before putting it back in service.
