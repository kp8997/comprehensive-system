package dev.kp8997._15_greedy._121_maximum_subarray;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public int maxSubArray(int[] nums) {
        if (nums.length == 1) return nums[0];

        int sum = 0;
        int max = Integer.MIN_VALUE;

        for (int n : nums) {
            sum += n;
            max = Math.max(max, sum);

            if (sum < 0) {
                sum = 0;
            }
        }
        return max;
    }
}
