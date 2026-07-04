We have list of collections for structure (column) and trait (row) below
        Array (single dimension)
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

    Queue
        PriorityQueue
        ArrayDeque

    Set
        HashSet
        TreeSet
        LinkedHashSet

    Map (Separate but related)
        HashMap
        TreeMap
        LinkedHashMap


======================================================
***General Concepts***
All of those implements Collection interface

We also have Collections (Utility class) for some static methods operations

======================================================
***Summary***

    When will I choose List, Set, Map?

    List: If the order of elements matters, or if you need to store duplicates.
        ArrayList
        LinkedList
    Set: If you need to store only unique elements (no duplicates).

    Map: If you need to store key-value pairs.

    When will I choose Array vs ArrayList?

    Array: When you need a fixed-size collection.

    ArrayList: When you need a dynamic-size collection.
