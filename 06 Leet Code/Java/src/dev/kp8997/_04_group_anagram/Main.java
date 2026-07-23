package dev.kp8997._04_group_anagram;

import java.util.*;

public class Main {
    static void main() {
        System.out.println(Solution.groupAnagrams(new String[]{"eat", "tea", "tan", "ate", "nat", "bat"}));
    }
}

class Solution {
    public static List<List<String>> groupAnagrams(String[] strs) {
        if (strs == null || strs.length == 0) return List.of(List.of(""));

        Map<String, List<String>> map = new HashMap<>();

        for (String s : strs) {
            char[] cs = s.toCharArray();
            Arrays.sort(cs);
            map.computeIfAbsent(String.valueOf(cs), k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(map.values());
    }
}
