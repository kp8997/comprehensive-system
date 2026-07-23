package dev.kp8997._02_anagram;

import java.util.Arrays;

public class Main {
    static void main() {
        System.out.println(Solution.isAnagram3("anagram", "nagaram"));
    }
}

class Solution {

    public static boolean isAnagram2(String s, String t) {
        return false;
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