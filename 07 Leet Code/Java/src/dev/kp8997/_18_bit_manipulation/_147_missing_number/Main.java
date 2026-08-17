package dev.kp8997._18_bit_manipulation._147_missing_number;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public int missingNumber(int[] nums) {
        int sum = 0;
        int total = nums.length * (nums.length + 1) / 2;
        for (int i = 0; i < nums.length; i++) {
            sum += nums[i];
        }
        return total - sum;
    }
}
