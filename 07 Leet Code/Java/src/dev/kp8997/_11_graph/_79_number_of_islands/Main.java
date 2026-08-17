package dev.kp8997._11_graph._79_number_of_islands;

public class Main {
    public static void main(String[] args) {
        char[][] grid = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}};

        char[][] grid1 = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };
        char[][] grid2 = {
                {'1','1','1'},
                {'0','1','0'},
                {'1','1','1'}
        };
        System.out.println("Grid 1: " + Solution.numIslands(grid));
        System.out.println("Grid 2: " + Solution.numIslands(grid1));
        System.out.println("Grid 2: " + Solution.numIslands(grid2));

        char[][] grid3 = {
                {'1', '1', '1', '1', '0'},
                {'1', '1', '0', '1', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '0', '0', '0'}};

        char[][] grid4 = {
                {'1', '1', '0', '0', '0'},
                {'1', '1', '0', '0', '0'},
                {'0', '0', '1', '0', '0'},
                {'0', '0', '0', '1', '1'}
        };
        char[][] grid5 = {
                {'1','1','1'},
                {'0','1','0'},
                {'1','1','1'}
        };
        //
        //System.out.println("Grid 3: " + Solution.numIslands2(grid3));
        //System.out.println("Grid 4: " + Solution.numIslands2(grid4));
        //System.out.println("Grid 5: " + Solution.numIslands2(grid5));
    }
}

class Solution {

    private static void dfsSink(char[][] grid, int r, int c) {
        // 1. Boundary & Base Case Check: Stop if out of bounds or water ('0')
        if (r < 0 || r >= grid.length || c < 0 || c >= grid[0].length || grid[r][c] != '1') {
            return;
        }

        // 2. Mark current cell as visited (sink land to water)
        grid[r][c] = '0';

        // 3. Recursively sink all 4 adjacent neighbors
        dfsSink(grid, r - 1, c); // Up
        dfsSink(grid, r + 1, c); // Down
        dfsSink(grid, r, c - 1); // Left
        dfsSink(grid, r, c + 1); // Right
    }

    public static int numIslands(char[][] grid) {
        int numberOfIsland = 0;
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[i].length; j++) {
                if (grid[i][j] == '1') {
                    numberOfIsland++;
                    dfsSink(grid, i, j);
                }
            }
        }
        return numberOfIsland;
    }

    // can not just check 4 nodes around item, because
    //public static int numIslands2(char[][] grid) {
    //    int numberOfIsland = 0;
    //    for (int i = 0; i < grid.length; i++) {
    //        for (int j = 0; j < grid[i].length; j++) {
    //            if (grid[i][j] == '1') {
    //                if ((i == 0 || grid[i-1][j] != '1')
    //                        && (j == 0 || grid[i][j-1] != '1')
    //                        //&& (j == grid[i].length - 1 || grid[i][j+1] != '1')
    //                        //&& (i == grid.length - 1 || grid[i+1][j] != '1')
    //                ) {
    //                    numberOfIsland++;
    //                }
    //            }
    //        }
    //    }
    //    return numberOfIsland;
    //}
}
