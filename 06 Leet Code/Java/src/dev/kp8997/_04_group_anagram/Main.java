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

    public static List<List<String>> groupAnagrams2(String[] strs) {
        if (strs == null || strs.length == 0) return List.of(List.of(""));

        Map<String, List<String>> map = new HashMap<>();

        for (String s : strs) {
            //char[] cs = s.toCharArray();
            //Arrays.sort(cs);
            // replace sort by character map by key like anagram solution with count

            int[] count = new int[26];

            // Count frequency of each character
            for (char c : s.toCharArray()) {
                count[c - 'a']++;
            }

            // Build a string key using StringBuilder (e.g., "#1#0#2...")
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < 26; i++) {
                sb.append('#');
                sb.append(count[i]);
            }
            String key = sb.toString();
            map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
        }

        return new ArrayList<>(map.values());
    }
}
