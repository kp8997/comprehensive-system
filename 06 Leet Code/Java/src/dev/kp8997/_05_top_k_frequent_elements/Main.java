package dev.kp8997._05_top_k_frequent_elements;

import java.util.*;
import java.util.stream.Collectors;

public class Main {
    static void main() {
        System.out.println(Arrays.toString(Solution.topKFrequent(new int[]{1, 1, 1, 2, 2, 3, 9, 0, 0, 0, 9,9,9,9}, 2)));
    }
}

class Solution {
    public static int[] topKFrequent(int[] nums, int k) {
        Map<Integer, Integer> map = new HashMap<>();

        for (int num : nums) {
            map.put(num, map.getOrDefault(num, 0) + 1);
        }

        System.out.println(map);
        System.out.println(map.entrySet());

        int[] keys = map.entrySet().
                stream().
                sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed()).
                limit(k).
                map(Map.Entry::getKey).
                mapToInt(Integer::intValue).
                toArray();

        return keys;
    }
}