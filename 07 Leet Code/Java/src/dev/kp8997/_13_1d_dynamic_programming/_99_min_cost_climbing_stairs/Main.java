package dev.kp8997._13_1d_dynamic_programming._99_min_cost_climbing_stairs;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {
    public int minCostClimbingStairs(int[] cost) {
        int one = 0;
        int two = 0;

        for (int i = cost.length - 1; i >= 0; i--) {
            cost[i] += Math.min(one, two);
            two = one;
            one = cost[i];
        }

        return Math.min(cost[0], cost[1]);
    }
}
