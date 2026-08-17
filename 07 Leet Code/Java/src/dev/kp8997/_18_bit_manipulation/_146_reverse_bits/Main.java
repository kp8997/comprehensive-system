package dev.kp8997._18_bit_manipulation._146_reverse_bits;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    // you need treat n as an unsigned value
    public int reverseBits(int n) {
        int ans = 0;

        for (int i = 0; i < 32; i++) {
            ans <<= 1;
            ans |= (n & 1);
            n >>= 1;
        }
        return ans;
    }
}
