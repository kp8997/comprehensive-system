package dev.kp8997._8_heap_priority_queue._62_k_clossest_points_to_origin;

import java.util.*;

public class Main {
    static void main() {
        int[][] points = {{1, 3}, {-2, 2}};
        int k = 1;

        int[][] points1 = {{3, 3}, {5, -1}, {-2, 4}};
        int k1 = 2;

        System.out.println(Arrays.deepToString(Solution.kClosest(points, k)));
        System.out.println(Arrays.deepToString(Solution.kClosest(points1, k1)));
    }
}

class Solution {
    public static int[][] kClosest(int[][] points, int k) {
        //PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[0] * a[0] + a[1] * a[1]).reversed());

        PriorityQueue<int[]> pq = new PriorityQueue<>((a, b) -> Integer.compare(
                b[0] * b[0] + b[1] * b[1], a[0] * a[0] + a[1] * a[1]
        ));

        for (int[] point : points) {
            pq.offer(point);

            if (pq.size() > k) {
                pq.poll();
            }
        }

        return pq.toArray(new int[k][]);
    }
}
