package dev.kp8997._07_product_of_arr_except_self;

import java.util.Arrays;

public class Main {
    static void main() {
        System.out.println(Arrays.toString(Solution.productExceptSelf3(new int[]{1, 2, 3, 4})));
        //System.out.println(Arrays.toString(Solution.productExceptSelf2(new int[]{-1, 1, 0, -3, 3})));
        System.out.println(Arrays.toString(Solution.productExceptSelf2(new int[]{1, 2, 3, 4})));
        System.out.println(Arrays.toString(Solution.productExceptSelf(new int[]{-1, 1, 0, -3, 3})));

    }
}

class Solution {

    public static int[] productExceptSelf(int[] nums) {
        int n = nums.length;
        int[] output = new int[n];

        // Step 1: Compute prefix products directly into output array
        output[0] = 1;
        for (int i = 1; i < n; i++) {
            output[i] = output[i - 1] * nums[i - 1];
        }

        // Step 2: Compute suffix products on the fly and combine
        int suffixProduct = 1;
        for (int i = n - 1; i >= 0; i--) {
            output[i] = output[i] * suffixProduct;
            suffixProduct *= nums[i]; // Update running suffix product
        }

        return output;
    }

    public static int[] productExceptSelf2(int[] nums) {
        int n = nums.length;
        int[] result = new int[n];
        int[] prefix = new int[n];
        int[] suffix = new int[n];

        // initialize the outside
        prefix[0] = 1;
        suffix[n - 1] = 1;

        for (int i = 1; i < n; i++) {
            prefix[i] = prefix[i - 1] * nums[i - 1];
            System.out.println("Prefix" + Arrays.toString(prefix));
        }

        for (int j = n - 2; j >= 0; j--) {
            suffix[j] = suffix[j + 1] * nums[j + 1];
            System.out.println("Suffix" + Arrays.toString(suffix));
        }

        for (int i = 0; i < n; i++) {
            result[i] = prefix[i] * suffix[i];
            System.out.println(Arrays.toString(result));

        }

        return result;
    }

    public static int[] productExceptSelf3(int[] nums) {
        int[] result = new int[nums.length];

        Arrays.fill(result, 1);

        for (int i = 0; i < nums.length; i++) {
            for (int j = 0; j < nums.length; j++) {
                if (i == j) continue;
                result[i] *= nums[j];
            }
        }

        return result;
    }
}
