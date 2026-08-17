package dev.kp8997._15_greedy._122_jump_game;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public boolean canJump(int[] nums) {
        int goal = nums.length - 1;
        for (int i = nums.length - 2; i >= 0; i--) {
            if (nums[i] + i >= goal) {
                goal = i;
            }
        }
        return goal == 0;
    }
}
