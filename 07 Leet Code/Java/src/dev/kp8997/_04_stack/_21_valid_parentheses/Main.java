package dev.kp8997._04_stack._21_valid_parentheses;

import java.util.ArrayDeque;
import java.util.Deque;

public class Main {
    static void main() {
        String s = "([}}])";
        String s1 = "))";

        System.out.println(Solution.isValid(s));
        System.out.println(Solution.isValid(s1));

    }
}

class Solution {
    public static boolean isValid(String s) {
        Deque<Character> stack = new ArrayDeque<>();

        if (s.length() % 2 != 0) return false;

        for (char c : s.toCharArray()) {
            if (c == '(' || c == '{' || c == '[') stack.push(c);

            if (stack.isEmpty()) return false;

            if (c == ')') {
                if (stack.peek() == '(') {
                    stack.pop();
                } else {
                    return false;
                }
            }

            if (c == '}') {
                if (stack.peek() == '{') {
                    stack.pop();
                } else {
                    return false;
                }
            }

            if (c == ']') {
                if (stack.peek() == '[') {
                    stack.pop();
                } else {
                    return false;
                }
            }
        }

        return stack.isEmpty();
    }
}
