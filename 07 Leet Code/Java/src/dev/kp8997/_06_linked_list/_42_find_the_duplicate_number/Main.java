package dev.kp8997._06_linked_list._42_find_the_duplicate_number;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

//Fast and slow pointer approach
// Time Complexity: O(n)
// Space Complexity: O(1)

class Solution {
    public int findDuplicate(int[] nums) {
        int slow = 0;
        int fast = 0;

        do {
            slow = nums[slow];
            fast = nums[nums[fast]];
        }

        while (slow != fast);

        int slow2 = 0;

        do {
            slow = nums[slow];
            slow2 = nums[slow2];
        }

        while (slow != slow2);

        return slow2;
    }
}
