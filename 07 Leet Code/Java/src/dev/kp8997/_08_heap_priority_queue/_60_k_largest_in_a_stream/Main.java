package dev.kp8997._08_heap_priority_queue._60_k_largest_in_a_stream;

import java.util.PriorityQueue;

public class Main {
    static void main() {
        KthLargest kthLargest = new KthLargest(3, new int[]{4, 5, 8, 2});
        System.out.println(kthLargest.add(3)); // return 4
        System.out.println(kthLargest.add(5)); // return 5
        System.out.println(kthLargest.add(10)); // return 5
        System.out.println(kthLargest.add(9)); // return 8
        System.out.println(kthLargest.add(4)); // return 8

    }
}

class KthLargest {
    private int k = 0;
    private final PriorityQueue<Integer> list;

    //public KthLargest(int k, int[] nums) {
    //    this.k = k;
    //    this.list = new PriorityQueue<>(Collections.reverseOrder());
    //
    //    for (int num : nums) {
    //        this.list.offer(num);
    //    }
    //}
    //
    //public int add(int val) {
    //    this.list.offer(val);
    //
    //    PriorityQueue<Integer> tempQueue = new PriorityQueue<>(this.list);
    //
    //    for (int i = 0; i < k - 1; i++) {
    //        tempQueue.poll();
    //    }
    //
    //    //System.out.println(tempQueue.peek());
    //    return tempQueue.peek();
    //}

    public KthLargest(int k, int[] nums) {
        this.k = k;
        this.list = new PriorityQueue<>(k);

        for (int num : nums) {
            this.add(num);
        }
    }

    public int add(int val) {
        this.list.offer(val);

        if (list.size() > k) {
            list.poll();
        }

        return list.peek();
    }
}

/**
 * Your KthLargest object will be instantiated and called as such:
 * KthLargest obj = new KthLargest(k, nums);
 * int param_1 = obj.add(val);
 */
