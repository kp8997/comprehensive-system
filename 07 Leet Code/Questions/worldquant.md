What does not belong to IPC
  ***Thread pooling***
  Message Queue
  Share Memory
  Named Pipes

What if we have 2 service that read right into database that have 2 nodes. And 2 nodes are replication with each other. What is its flaw point?
  ***sync latency***
  if the main db has a downtime, the reads will be impacted?

Multiple choices: Partition Database
  ***Vertical (column) parition***
  ***Horizontal (row) parition***
  ***can not combine both of them into the same table***
  Round-robin partition

Statement is not true between thread and process
  ***Thread can not share memory to each other***
  One process cannot read from or write to the memory section of another process.
