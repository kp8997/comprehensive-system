package dev.kp8997._18_bit_manipulation._144_number_of_1_bits;

public class Main {
    public static void main(String[] args) {
        
    }
}

//standard way
class Solution {

    // you need to treat n as an unsigned value
    public int hammingWeight(int n) {
        int count = 0;
        int mask = 1;
        for (int i = 0; i < 32; i++) {
            if ((n & mask) != 0) {
                count++;
            }
            n >>= 1;
        }
        return count;
    }
}

//smart way
class Solution1 {

    // you need to treat n as an unsigned value
    public int hammingWeight(int n) {
        int count = 0;
        while (n != 0) {
            n = n & (n - 1);
            count++;
        }
        return count;
    }
}
