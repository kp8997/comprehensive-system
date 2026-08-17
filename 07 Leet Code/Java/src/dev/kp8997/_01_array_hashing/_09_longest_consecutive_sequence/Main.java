package dev.kp8997._1_array_hashing._09_longest_consecutive_sequence;

import java.util.HashSet;
import java.util.Set;

public class Main {
    static void main() {
        int[] nums = new int[]{0,3,7,2,5,8,4,6,0,1};

        System.out.println(Solution.longestConsecutive(nums));
    }
}

class Solution {
    public static int longestConsecutive(int[] nums) {
        if (nums == null || nums.length == 0) return 0;

        Set<Integer> set = new HashSet<>();

        for (int n : nums) {
            set.add(n);
        }

        int seq = 1;
        int tempSeq = 1;

        for (int n : set) {
            System.out.println("n: " + n);
            if (!set.contains(n - 1)) {
                while (set.contains(n + 1)) {
                    tempSeq++;
                    n++;

                    System.out.println("tempSeq " + tempSeq);
                }

                if (seq < tempSeq) {
                    seq = tempSeq;
                }
            }
            tempSeq = 1;
        }

        return seq;
    }
}