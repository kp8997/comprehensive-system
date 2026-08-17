package dev.kp8997._18_bit_manipulation._145_counting_bits;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {
    public int[] countBits(int n) {
        int res[] = new int[n + 1];
        for (int i = 1; i <= n; i++)
            res[i] = 1 + res[i & (i - 1)];
        return res;
    }
}
