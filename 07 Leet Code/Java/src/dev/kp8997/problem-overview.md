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

  Binary Search: Halving a monotonic search space O(log N) instead of scanning linearly.
    Define boundaries low and high. Calculate mid = low + (high - low) / 2.
		Binary Search on Values / Condition (Predicate Search): If searching for a threshold (e.g., Koko Eating Bananas), construct a monotonic boolean function isFeasible(mid) and adjust low/high accordingly.
		Rotated Arrays: Identify which half of the array is sorted (nums[low] <= nums[mid] or nums[mid] <= nums[high]), then determine if the target lies within that strictly sorted half.

  Tree - BST: Structural Recursion via Depth-First Search (DFS) or level-by-level processing via Breadth-First Search (BFS).
  	DFS (Pre-order, In-order, Post-order): Trust the recursive leap of faith: solve the base case (root == null), delegate to dfs(root.left) and dfs(root.right), then aggregate results at the parent (e.g., Maximum Depth, Lowest Common Ancestor).
		BFS (Level-Order): Use a Queue. Measure queue.size() at each level to process nodes layer-by-layer (e.g., Binary Tree Level Order Traversal, Right Side View).
		BST Property Exploitation: In a Binary Search Tree, left < root < right. Use this invariant to search or validate in O(H) time without exploring both subtrees.

  Tries - Prefix Trees: A specialized N-ary tree storing character transitions for efficient O(L) prefix lookups.
    1. Each TrieNode contains a fixed-size array/map of child references (e.g., children[26]) and a boolean flag isEndOfWord.
	  2. Iterate through each character of a string to traverse or instantiate child nodes sequentially (e.g., Implement Trie, Word Search II).

Part 3 - Graphs & State Space Exploration

  Graph & Advance Graph: Modeling entities (nodes) and relationships (edges), traversing them while avoiding infinite cycles via visitation tracking.
    1.	Grid/Matrix as Graph: Treat cells as nodes and the 4/8 cardinal directions as edges. Use in-place marking or a visited set (e.g., Number of Islands, Rotting Oranges).
	  2.	Topological Sort (Kahn’s Algorithm / Post-order DFS): Compute in-degree for all nodes in a Directed Acyclic Graph (DAG). Add 0-in-degree nodes to a queue to determine execution order (e.g., Course Schedule).
	  3.	Shortest Path (Dijkstra's Algorithm): Use a Min-Heap storing (cost, node) to greedily expand the cheapest path in graphs with non-negative edge weights (e.g., Network Delay Time).
	  4.	Disjoint Set Union (Union-Find): Maintain dynamic connected components with find() (with path compression) and union() (by rank/size) (e.g., Redundant Connection, Graph Valid Tree).

  Backtracking: Systematic exhaustive search of a decision tree by choosing, exploring recursively, and undoing the choice (pruning paths that violate constraints).
     ```
     def backtrack(state, choices):
      if is_solution(state):
        result.add(state.copy())
        return
      for choice in choices:
        if is_valid(choice):
            make_choice(choice)      # 1. Choose
            backtrack(new_state)     # 2. Explore
            undo_choice(choice)      # 3. Revert (Backtrack)
    ```

  Heap / Priority Queue: Continuous dynamic tracking of the Extreme Value (Max or Min) in O(log N) insertion and O(1) retrieval.
    1. Top-K Elements: Maintain a Min-Heap of fixed capacity $K$. Once size exceeds $K$, evict the minimum element so the heap retains the $K$ largest elements in O(N log K).
	  2. Two Heaps Pattern: Maintain a Max-Heap for the smaller half of data and a Min-Heap for the larger half to extract the median dynamically in O(1) (e.g., Find Median from Data Stream).

  Dynamic Programming: 1D - 2D: Breaking an optimization problem into overlapping subproblems with optimal substructure, caching intermediate answers to avoid exponential re-computation.
    1. Identify the State: What parameters uniquely define a scenario? (e.g., dp[i] for index $i$, dp[i][w] for index $i$ and remaining capacity $w$).
	  2.
      Formulate the State Transition Equation (Recurrence Relation):
		  Decision: dp[i] = max(dp[i - 1], dp[i - 2] + nums[i]) (e.g., House Robber).
		  Combinations: dp[i] = sum(dp[i - coin]) (e.g., Coin Change).
		  Grid/String Matching: dp[i][j] = dp[i-1][j-1] + 1 if s[i] == t[j] (e.g., Longest Common Subsequence, Edit Distance).
	  3. Space Optimization: If computing the current row/step only requires the immediate previous row/step, compress an $O(N^2)$ table into $O(N)$ or $O(1)$ scalar variables.

  Greedy: Making the locally optimal choice at each step with the guarantee that it leads to a globally optimal solution (without backtracking).
    1. Local Optimality: The core principle is that the best choice for the current step does not prevent optimal future choices (e.g., buying low/selling high, picking the shortest finishing interval).
  	2. Proof by Exchange Argument: To prove a greedy strategy works, assume there is an optimal solution that differs at step k. Show that you can swap the greedy choice into that solution without worsening the result.
	  3. Problem Types: Most commonly applied to Interval problems (sort by end time), Knapsack problems (fractional), andscheduling algorithms.

  Interval: Problems involving the scheduling, merging, or partitioning of ranges on a line.
    Sort First: The critical first step is always sorting the intervals, typically by the start time, or sometimes by the end time depending on the goal.
	Calculate the result: then calculate merge or remove or something else
	Return the result: often calculate the non-overlapping interval first
    then calculate merge or remove or something else
    then return the result
    often calculate the non-overlapping interval first

  Math & Bit Manipulation: Exploiting binary representation, modular arithmetic, and base-2 bitwise algebraic laws.
    1.	XOR Cancellation: A ^ A = 0 and A ^ 0 = A. Used to eliminate paired duplicates (e.g., Single Number).
	  2.	Bit Clearing Trick: n & (n - 1) systematically flips the least significant set bit (1) to 0. Ideal for counting set bits in O(number of 1s) (e.g., Number of 1 Bits, Counting Bits).
    3.	Bit Shifts: n >> 1 (divide by 2) and n << 1 (multiply by 2) for rapid arithmetic manipulation without overflow.
