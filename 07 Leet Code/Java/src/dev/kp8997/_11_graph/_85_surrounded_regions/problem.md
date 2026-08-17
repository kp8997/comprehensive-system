# Surrounded Regions

You are given an `m x n` matrix `board` containing **letters** `&#39;X&#39;` and `&#39;O&#39;`, **capture regions** that are **surrounded**:



	- **Connect**: A cell is connected to adjacent cells horizontally or vertically.
	- **Region**: To form a region **connect every** `&#39;O&#39;` cell.
	- **Surround**: A region is surrounded if none of the `&#39;O&#39;` cells in that region are on the edge of the board. Such regions are **completely enclosed **by `&#39;X&#39;` cells.


To capture a **surrounded region**, replace all `&#39;O&#39;`s with `&#39;X&#39;`s **in-place** within the original board. You do not need to return anything.


 

Example 1:



**Input:** board = [["X","X","X","X"],["X","O","O","X"],["X","X","O","X"],["X","O","X","X"]]


**Output:** [["X","X","X","X"],["X","X","X","X"],["X","X","X","X"],["X","O","X","X"]]


**Explanation:**


In the above diagram, the bottom region is not captured because it is on the edge of the board and cannot be surrounded.



Example 2:



**Input:** board = [["X"]]


**Output:** [["X"]]



 

**Constraints:**



	- `m == board.length`
	- `n == board[i].length`
	- `1 <= m, n <= 200`
	- `board[i][j]` is `&#39;X&#39;` or `&#39;O&#39;`.