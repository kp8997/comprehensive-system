Part 1 - Linear & Sequential Structures

  Array & Hashing: Trade space for time
    Hash Map for counter
    Hash Set for unique values
    Pre-computing using prefix/suffix array when a problem asks for aggregate results without modifying original sequences

  Two pointers: Exploiting sorted order or converging/diverging boundaries to eliminate redundant nested loops.
    Opposite Ends (Converging): Place left = 0 and right = n - 1. If the current sum/metric is too small, increment left; if too large, decrement right (e.g., Two Sum II, Container With Most Water, Trapping Rain Water).
    Fast & Slow (Same Direction): Place one pointer to read/scan and another to write/partition in-place without auxiliary space.

  Sliding Window: Tracking a contiguous sub-segment (window) of an array or string that expands or contracts dynamically based on constraints.
    Fixed Size (Window Size as Variable): Often used for String/Array/Linked-List problems where you must maintain exactly K elements (e.g., Longest Substring with K Distinct Characters, Rotate Array).
    Dynamic Size (Based on Property): The window expands from the right and contracts from the left to maintain a certain property (e.g., sum >= target, valid parentheses). The loop typically uses a while (left < right) construct inside the for loop (e.g., Minimum Size Subarray Sum, Fruit Into Baskets).
    Circular Array Adaptation: Use the modulo operator (i % n) to wrap around, allowing a fixed-length array to behave as circular for problems like Maximum Sum Circular Subarray.

  Stack
    Matching/Parsing: Push opening tokens; upon encountering a closing token, pop and validate the match (e.g., Valid Parentheses).
    Monotonic Stack: Maintain elements in strictly increasing or decreasing order. As you iterate, pop all elements violated by the incoming item to immediately find the "Next Greater Element" or "Previous Smaller Element" in linear O(N) time (e.g., Daily Temperatures, Largest Rectangle in Histogram).

  LinkedList: Pointer manipulation, in-place sequence rewiring, and non-contiguous memory management.
    Dummy Head Technique: Always initialize dummy = ListNode(0, head) to eliminate edge cases around removing or inserting the head node.
	 	Fast & Slow Pointers (Floyd's Cycle Finding): Move slow by 1 step, fast by 2 steps to find the middle of the list or detect cycles (e.g., Linked List Cycle, Reorder List).
		Three-Pointer In-Place Reversal: Maintain prev, curr, and temp_next to reverse links without extra space (e.g., Reverse Linked List, Reverse Nodes in k-Group).

Part 2 - Search & Hierarchical Traversal

  Binary Search

  Tree - BST

  Tries - Prefix Trees

Part 3 - Graphs & State Space Exploration

  Graph & Advance Graph

  Backtracking

  Heap / Priority Queue

  Dynamic Programming: 1D - 2D

  Greedy

  Interval
    sort first
    then calculate merge or remove or something else
    then return the result
    often calculate the non-overlapping interval first

  Math & Bit Manipulation
