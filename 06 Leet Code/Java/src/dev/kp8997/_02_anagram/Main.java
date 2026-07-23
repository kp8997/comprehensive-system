package dev.kp8997._02_anagram;

import java.util.Arrays;
import java.util.HashMap;

public class Main {
    static void main() {
        System.out.println(Solution.isAnagram3("anagram", "nagaram"));
        System.out.println(Solution.isAnagram2("anagramr", "nagarram"));
        System.out.println(Solution.isAnagram1("anagram", "nagaram"));

        int [] count = new int[26];

        count[5]++;
        System.out.println(Arrays.toString(count));
    }
}

class Solution {

    // Time is O(n), space is O(1)
    public static boolean isAnagram1(String s, String t) {
        if (s.length() != t.length()) {
            return false;
        }

        int[] counts = new int [26];
        for (int i = 0; i < s.length(); i++) {
            counts[s.charAt(i) - 'a']++;
            counts[t.charAt(i) - 'a']--;
        }

        for (int count : counts) {
            if (count != 0) return false;
        }

        return true;
    }

    // Time is O(n), space is O(k), k is number of unique characters
    public static boolean isAnagram2(String s, String t) {

        if (s.length() != t.length()) {
            return false;
        }

        HashMap<Character, Integer> s1 = new HashMap<Character, Integer>();

        for (char c : s.toCharArray()) {
            s1.put(c, s1.getOrDefault(c, 0) + 1);
        }

        for (char c : t.toCharArray()) {
            if (!s1.containsKey(c) || s1.get(c) == 0) return false;

            int count = s1.get(c) - 1;
            if (count == 0) {
                s1.remove(c);
            } else {
                s1.put(c, count);
            }
        }

        return s1.isEmpty();
    }

    // Time is O(nlogn), space is n
    public static boolean isAnagram3(String s, String t) {
        char [] s1 = s.toCharArray();
        char [] s2 = t.toCharArray();

        Arrays.sort(s1);
        Arrays.sort(s2);

        if (s1.length == s2.length) {
            for (var i = 0; i < s1.length; i++) {
                if (s1[i] != s2[i]) {
                    return false;
                }
            }

            return true;
        }

        return false;
    }
}