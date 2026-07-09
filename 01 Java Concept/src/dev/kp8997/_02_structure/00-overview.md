
## We have list of collections for structure (column) and trait (row) below.
        Array: Back by fixed size, static structure, primitive type

    List: An ordered collection that maintains insertion order and allows duplicate elements. You can access elements instantly using an index.
        LinkedList:
            Property: Backed by a doubly-linked sequence of nodes.
            When: Application need constantly inserts or deletes elements from the beginning or middle of the collection.
            Why: Inserting elements only requires changing pointer references (O(1)), avoiding the costly array-copying shifts required by an ArrayList.
            E.g: Implementing a real-time undo/redo buffer in an application interface.
        ArrayList:
            Property: Backed by a dynamic, resizable array.
            When: Need fast, random access to elements by their index, and your data size changes infrequently.
            Why: It provides O(1) constant-time lookup performance via indexing.
            E.g: Fetching a read-only list of product catalog items to display on a webpage.

    Queue/Deque:
        LinkedList: The same as above, they also implements Deque interface
        ArrayDeque:
            Property: Stack, you need a pure FIFO queue or a LIFO (Last-In, First-Out) stack.
            When: Managing an asynchronous message buffer or task pipeline where requests must be processed in the exact order they hit the server.
            Why: It is significantly faster and uses less memory than Stack or LinkedList.
            E.g: Managing an asynchronous message buffer or task pipeline where requests must be processed in the exact order they hit the server.
        PriorityQueue:
            Property: a PriorityQueue is backed by a Priority Heap (unbounded balanced binary heap binary tree). Elements are processed based on their natural priority or a custom comparator (not FIFO).
            When: Items have varying emergency levels and cannot simply be processed by arrival time.
            Why: Elements are positioned based on their priority, determined by natural ordering or a Comparator supplied at instantiation time. The element with the highest priority is always placed at the head of the queue and disgorged first (O(log n) for insertion and extraction).
            E.g: A network router triaging data packets, where premium-tier user traffic bypasses standard traffic.


    Set: Sets focus strictly on uniqueness. They prohibit duplicate elements. If you try to add an existing element, the operation returns false.
        HashSet: 
            Property: Backed by a hash table; offers no order guarantees.
            When: need high-performance storage and lookup for unique items
            Why: It provides rapid O(1) time complexity for basic operations like add, remove, and contains.
            E.g: Storing a collection of unique IP addresses filtering through a firewall to prevent duplication logs.
        TreeSet: 
            Property: Backed by a Red-Black tree; elements are sorted naturally or by a custom comparator.
            When: Your elements must automatically stay sorted at all times.
            Why: Instead, every time an element is added, the tree balances itself, ensuring that elements are dynamically ordered either by their natural ordering or by a custom Comparator. Because it operates as a tree structure, operations like add, remove, and contains take O(log n) logarithmic time.
            E.g: Managing a live leaderboard of high scores where elements must remain in ascending or descending order.
        LinkedHashSet:
            Property:  Maintains a doubly-linked list running through the hash table to preserve insertion order.
            When: need a collection of unique items but must preserve the exact chronological sequence in which they arrived.
            Why: uses the doubly-linked list to record the exact chronological order in which elements are inserted. It maintains O(1) time complexity for fundamental operations (add, contains, remove) while adding a tiny memory overhead for maintaining the node pointers.
            E.g: Building a "Recently Viewed Items" widget where duplicates are ignored but the history order matters.

    Map (Separate from collection framework but related)
        HashMap: 
            Property: High-speed, unsorted key-value storage.
            When: need to instantly look up records using a unique ID or key.
            Why: it jumps directly to the calculated bucket index, it bypasses iterative loops entirely, offering blistering $O(1)$ constant-time performance for get() and put() operations. It offers no order guarantees.
            E.g: Creating an in-memory session cache where a unique SessionID instantly fetches a user's profile details.
        TreeMap: 
            Property: Keys are sorted in natural or custom order.
            When: need key-value pairs sorted dynamically by the keys.
            Why: It implements the SortedMap interface and relies on a Red-Black Tree structure. Instead of organizing keys by hash codes, keys are compared against one another and sorted dynamically in natural alphabetical/numerical order, or via a custom Comparator. Key lookups, insertions, and deletions run in $O(\log n)$ time.
            E.g: Generating an alphabetical dictionary directory or a chronological calendar timeline map where keys are timestamps.
        LinkedHashMap:
            Property: Maintains insertion order of keys.
            When: Implementing a fixed-capacity LRU (Least Recently Used) Cache for database queries.
            Why: It is a direct subclass of HashMap, meaning it retains the high-speed hashing structure. However, it introduces a doubly-linked list running through all of its key-value pairs. This dual-architecture setup tracks the insertion order of your keys. It takes slightly more memory than a standard HashMap to store the pointer nodes but preserves predictable iteration order.
            E.g: LRU for tabs of VScode or Antigravity

======================================================

***General Concepts***

- All of those implements Collection interface

- We also have Collections (Utility class) for some static methods operations

======================================================

***Summary***

- When will I choose structure List, Set, Map?

    List: If the order of elements matters, or if you need to store duplicates.

    Set: If you need to store only unique elements (no duplicates).

    Map: If you need to store key-value pairs.

    When will I choose Array vs ArrayList?

    Array: When you need a fixed-size collection.

    ArrayList: When you need a dynamic-size collection.

- When i choose property after structure:

    Hash: Use hash for faster lookups
    
    Tree: Use tree for sorted order
    
    Linked: Use linked list for insertion order

    Queue: For easy access and retrieve by order direction such as FIFO or LIFO
        

======================================================

***Advance Insight Question***

What case and why we need:

    sorted order:

    faster lookup:

    insertion order:

    FIFO and LIFO:

    