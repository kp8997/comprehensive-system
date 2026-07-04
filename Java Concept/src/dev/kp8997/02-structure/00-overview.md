
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
            Property:
            When:
            Why:
            E.g:

    Set: Sets focus strictly on uniqueness. They prohibit duplicate elements. If you try to add an existing element, the operation returns false.
        HashSet: Backed by a hash table; offers no order guarantees.
            Property:
            When
            Why
            E.g:
        TreeSet: Backed by a Red-Black tree; elements are sorted naturally or by a custom comparator.
            Property:
            When
            Why
            E.g:
        LinkedHashSet: Maintains a doubly-linked list running through the hash table to preserve insertion order.
            Property:
            When
            Why
            E.g:

    Map (Separate from collection framework but related)
        HashMap
            Property:
            When
            Why
            E.g:
        TreeMap
            Property:
            When
            Why
            E.g:
        LinkedHashMap
            Property:
            When
            Why
            E.g:

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
    