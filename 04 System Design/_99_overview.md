
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
