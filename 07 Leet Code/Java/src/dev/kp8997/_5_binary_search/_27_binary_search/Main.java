package dev.kp8997._5_binary_search._27_binary_search;

public class Main {
    static void main() {
        int[] nums = {-1,0,3,5,9,12};

        System.out.println(Solution.search(nums, 9));
    }
}

class Solution {
    public static int search(int[] nums, int target) {
        int left = 0;
        int right = nums.length - 1;

        while (left <= right) {
            int middle = left + ((right - left)/ 2);

            if (target > nums[middle]) {
                left = middle + 1;
            } else if (target < nums[middle]) {
                right = middle - 1;
            } else if (target == nums[middle]) {
                return middle;
            }
        }

        return -1;
    }
}
