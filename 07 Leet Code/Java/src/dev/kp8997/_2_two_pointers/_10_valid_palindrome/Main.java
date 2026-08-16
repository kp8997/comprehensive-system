package dev.kp8997._2_two_pointers._10_valid_palindrome;

public class Main {

    static void main() {
        String s = "A man, a plan, a canal: Panama";
        System.out.println(Solution.isPalindrome(s));
    }
}

class Solution {
    public static boolean isPalindrome(String s) {
        // sanitize

        StringBuilder sb = new StringBuilder();

        for (char c : s.toCharArray()) {
            if (Character.isLetterOrDigit(c)) {
                sb.append(Character.toLowerCase(c));
            }
        }
        var s1 = sb.toString();

        int len = s1.length();
        for (int i = 0; i < len; i++) {
            System.out.println("s1.charAt(i) " + s1.charAt(i));
            System.out.println("s1.charAt(len - 1 - i) " + s1.charAt(len - 1 - i));

            if (s1.charAt(i) != s1.charAt(len - 1 - i)) {
                return false;
            }
        }

        return true;
    }
}