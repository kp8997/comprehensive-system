package dev.kp8997._18_bit_manipulation._143_single_number;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

//We can use xor operation as it cancel out itself (i.e. only when values are different in binary representation then give output). See how xor operation works if confused.
class Solution {

    public int singleNumber(int[] nums) {
        int ans = nums[0];
        for (int i = 1; i < nums.length; i++) ans ^= nums[i];
        return ans;
    }
}
