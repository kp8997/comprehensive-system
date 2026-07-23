package dev.kp8997._02_anagram;

import java.util.Arrays;

public class Main {
    static void main() {
        System.out.println(Solution.isAnagram3("anagram", "nagaram"));
        System.out.println(Solution.isAnagram2("anagram", "nagaram"));

        int [] count = new int[26];

        count[5]++;
        System.out.println(Arrays.toString(count));
    }
}

class Solution {

    public static boolean isAnagram2(String s, String t) {
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