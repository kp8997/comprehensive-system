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

    Cassandra: they use ring architecture system
        Every nodes connect to each other in a ring topology.
        There is no master node, every node is equal.
        Data is distributed across the ring based on the hash of the partition key.
        They can do the write/read operation on each node. And then sync to each other (replication) to make sure every nodes have synced


Some issues which shards:

    Hard to join across shards

    Re-sharding is also a problem. When some node is full and we need to add new node, we need to move data from old node to new node, which is a very expensive operation.

    Hotspot: data might not be distributed evenly across shards, which can cause some shards to be overloaded while others are underloaded.
        Modern database algorithm using hash function to distribute data evenly across shards. They can also repartition, re-shard things, re-route the traffic.

Some note on NoSQL:

    Consider when designing data, if we only need to look-up by key value mechanics, nosql is the best choice. No need for formal schema like SQL and is suitable for really big data.

Why normalization and what is denormalization:

    denormalization: is duplicated data over a or many tables, make the insertion, update, deletion not efficient, but make the reading process more efficient.

    normalization: is removing duplicate data, and make it more efficient to store data, but make the reading process more complex.
