package dev.kp8997._02_two_pointers._13_container_with_most_water;

public class Main {
    static void main() {

    }
}

class Solution {
    public static int maxAreaBruteForce(int[] height) {
        int maxWater = 0;
        int n = height.length;

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                int width = j - i;
                int currentHeight = Math.min(height[i], height[j]);
                int currentWater = width * currentHeight;

                maxWater = Math.max(maxWater, currentWater);
            }
        }

        return maxWater;
    }

    public static int maxArea(int[] height) {
        int maxWater = 0;
        int left = 0;
        int right = height.length - 1;

        while (left < right) {
            // Calculate current width and height
            int width = right - left;
            int currentHeight = Math.min(height[left], height[right]);
            int currentWater = width * currentHeight;

            // Track maximum area
            maxWater = Math.max(maxWater, currentWater);

            // Move the pointer with the smaller height
            if (height[left] < height[right]) {
                left++;
            } else {
                right--;
            }
        }

        return maxWater;
    }
}
