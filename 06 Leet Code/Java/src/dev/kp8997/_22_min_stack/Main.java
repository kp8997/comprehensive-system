package dev.kp8997._22_min_stack;

import java.util.*;

public class Main {
    static void main() {
        MinStack minStack = new MinStack();
        minStack.push(-2);
        minStack.push(0);
        minStack.push(-3);
        System.out.println(minStack.getMin()); // return -3
        minStack.pop();
        System.out.println(minStack.top());    // return 0
        System.out.println(minStack.getMin()); // return -2
    }
}

class MinStack {
    Deque<Integer> list;
    Deque<Integer> minList;

    public MinStack() {
        list = new ArrayDeque<>();
        minList = new ArrayDeque<>();
    }

    public void push(int value) {
        list.push(value);
        if (minList.isEmpty() || minList.peek() >= value) {
            minList.push(value);
        }
    }

    public void pop() {
        int removed = list.pop();
        if (!minList.isEmpty() && removed == minList.peek()) {
            minList.pop();
        }
    }

    public int top() {
        return list.isEmpty() ? 0 : list.peek();
    }

    public int getMin() {
        return minList.isEmpty() ? 0 : minList.peek();
    }
}
