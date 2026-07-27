It is geographically distributed network of proxy servers and their data center.

The content delivery network (CDN) helps to reduce the load on the origin server by caching the content on the edge servers. It's main benefit is to reduce latency and increase throughput. It can avoid the traffic jam for the origin server.

It is useful for static content: image, video, audio, static web pages


---------------------------------------

Resilience solution when server is physically crash (network down, server off, catastrophe...)
    1. Distribute all over the world. Just change DNS to nearest alive zone
    2. Implement a redundancy capability for servers to handle those cases if happen
        Should consider the cost and demand. Does that meet our requirement
    
    

---------------------------------------

Distributed storage system
    We can use cloud service from Google, Amazon, Microsoft

---------------------------------------

(HDFS) Hadoop distributed file system:

    What: Hadoop was created to solve two fundamental problems that traditional systems couldn't handle: The Storage Problem and The Compute Problem.
    
    Why: raditional relational database management systems (RDBMS) and data warehouses hit a massive technological bottleneck. They were simply not built to store or process petabytes of unstructured data economically.

    When: It fits for big, long-term, unstructured, data storage and AI model training/inferencing staging environment.

Traditional:
    To store more data in a database like MySQL or Oracle, you had to upgrade to a bigger, more expensive mainframe machine (Vertical Scaling).
    Single servers hit physical limits on drive slots, disk throughput, and memory. Moreover, enterprise storage arrays (like SAN/NAS) were astronomically expensive.
    => HDFS allows you to combine thousands of cheap, off-the-shelf commodity servers into a single logical file system. Instead of buying a $1,000,000 enterprise server, you connect one hundred $2,000 PC nodes.

    Data was stored on a central storage server. When you ran an analytics query, the computing server had to pull gigabytes or terabytes of data over the network network switch to process it in memory.
    Network bandwidth becomes the primary bottleneck. Transferring petabytes of data over cables causes massive latency and network congestion.
    => Hadoop inverted this pattern. Instead of moving data to the code, Hadoop moves the code (MapReduce job) to the node where the data already lives.


Mechanism: master - slave
    Data storage: Data is split into blocks (default 64MB) and replicated across multiple slave nodes. The master node keeps track of which blocks are stored on which slave nodes.
    
    Name Node (master): There may have more than one node, usually are 3 to hold the metadata
        Metadata (file names, permissions, and mappings of file blocks to DataNodes). 
        Think of it like a indexes of file blocks (similar to DB indexes)
        Handle client requests for file operations (read, write, delete)
        
    Data Node (slave)
        Store blocks of data
        Manage storage, block operations, and block replication
        