package dev.kp8997._02_two_pointers._11_2_sum_sorted_arr;

import java.util.Arrays;

public class Main {
    static void main() {
        int[] n = new int[]{2,7,11,15};
        int[] n1 = new int[]{-1,0};

        System.out.println(Arrays.toString(Solution.twoSum(n, 26)));
        System.out.println(Arrays.toString(Solution.twoSum(n1, -1)));

    }
}

class Solution {
    public static int[] twoSum(int[] numbers, int target) {
        // numbers is sorted in this case
        if (numbers == null || numbers.length == 0) return new int[]{};

        int left = 0;
        int right = numbers.length - 1;

        while (left < right) {
            int sum = numbers[left] + numbers[right];

            if (sum == target) {
                return new int[]{left + 1, right + 1};
            }
            else if (sum < target) {
                left++;
            }
            else {
                right--;
            }
        }

        return new int[]{};
    }
}
