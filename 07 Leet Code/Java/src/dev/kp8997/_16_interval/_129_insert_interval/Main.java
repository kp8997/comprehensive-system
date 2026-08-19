package dev.kp8997._16_interval._129_insert_interval;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int[][] intervals = new int[][]{{1, 3}, {6, 9}};
        int[] newInterval = {2, 5};

        System.out.println(Arrays.deepToString(new Solution1().insert(intervals, newInterval)));

        int[][] intervals1 = {{1, 2}, {3, 5}, {6, 7}, {8, 10}, {12, 16}};
        int[] newInterval1 = {4, 8};
        System.out.println(Arrays.deepToString(new Solution1().insert(intervals1, newInterval1)));

    }
}

class Solution1 {
    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> list = new ArrayList<>();
        for (int[] interval : intervals) {
            //System.out.println(Arrays.toString(interval));
            //if (newInterval[0] > interval[1]) {
            //    continue;
            //}
            if (newInterval == null || interval[1] < newInterval[0]) {
                list.add(interval);
            } else if (interval[0] < newInterval[0]) {
                if (interval[1] > newInterval[1]) {
                    newInterval[1] = interval[1];
                }
            }
        }

        return list.toArray(new int[list.size()][]);
    }
}


class Solution {

    public int[][] insert(int[][] intervals, int[] newInterval) {
        List<int[]> res = new ArrayList<>();
        for (int[] interval : intervals) {
            if (newInterval == null || interval[1] < newInterval[0]) res.add(
                    interval
            );
            else if (interval[0] > newInterval[1]) {
                res.add(newInterval);
                res.add(interval);
                newInterval = null;
            } else {
                newInterval[0] = Math.min(interval[0], newInterval[0]);
                newInterval[1] = Math.max(interval[1], newInterval[1]);
            }
        }
        if (newInterval != null) res.add(newInterval);
        return res.toArray(new int[res.size()][]);
    }
}
