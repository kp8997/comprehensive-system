package dev.kp8997._18_bit_manipulation._148_sum_of_two_integers;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public int getSum(int a, int b) {
        while (b != 0) {
            int tmp = (a & b) << 1;
            a = (a ^ b);
            b = tmp;
        }
        return a;
    }
}
