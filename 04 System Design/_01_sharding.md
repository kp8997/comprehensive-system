Sharding: 

    What:
        A shard is just a horizontal partition of database (a horizontal node of db).
        Unlike replication—where every server holds a copy of the same data—sharding divides the data itself. Each shard holds a unique subset of the total dataset, acting as an independent database.

    Why:
        When a monolithic database grows exponentially, it eventually hits physical and architectural bottlenecks:
        Hardware Limits (Vertical Scaling Wall): You can only add so much CPU, RAM, and NVMe storage to a single machine before it becomes economically or physically impossible to scale higher.
        Throughput Bottlenecks: A single server can only handle a finite number of concurrent Read/Write operations.
        Index Size Inflation: As tables grow to hundreds of millions of rows, their search indexes overflow the server's RAM, forcing slow disk I/O operations.

3 things to consider when comply database design:
    High availability
    Redundancy
    Fault Tolerance

Design example:

    MongoDB: they use architecture replicas set.
    In this architecture, we will have 3 components:
        - Mongos: contain router rule, it will route the query to the correct shard.
        - Config servers: store the metadata of the cluster, including the shard key and the range of values. It has replica it self to serve the fault tolerance for configuration.
        - Replica sets (shards): store the data, each replica set is a subset of the data itself. Inside, a replica set has primary-secondary nodes to serve the redundancy for data. It's like replication but on a smaller scale. When a primary node is down, a secondary node will be elected as the new primary to serve the high availability.
