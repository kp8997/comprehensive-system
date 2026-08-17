package dev.kp8997._8_heap_priority_queue._61_last_stone_weight;

import java.util.Collections;
import java.util.PriorityQueue;

public class Main {
    static void main() {
        int[] stones = {2,7,4,1,8,1};
        System.out.println(Solution.lastStoneWeight(stones));
    }
}

class Solution {
    public static int lastStoneWeight(int[] stones) {

        PriorityQueue<Integer> pq = new PriorityQueue<>(Collections.reverseOrder());

        for (int n : stones) {
            pq.offer(n);
        }

        while (pq.size() > 1) {
            int first = pq.poll();
            int second = pq.poll();

            int valueLeft = first - second;
            if (valueLeft > 0) {
                pq.offer(valueLeft);
            }
        }

        return pq.isEmpty() ? 0: pq.peek();
    }
}
