package dev.kp8997._16_interval._130_merge_intervals;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int[][] intervals = {{1, 3}, {1, 5}, {6, 7}};
        System.out.println(Arrays.deepToString(new Solution1().merge(intervals)));

        int[][] intervals1 = {{1, 2}, {2, 3}};
        System.out.println(Arrays.deepToString(new Solution1().merge(intervals1)));
    }
}

class Solution1 {
    public int[][] merge(int[][] intervals) {
        List<int[]> list = new ArrayList<>();
        // check null
        if (intervals == null || intervals.length == 0) {
            return list.toArray(new int[list.size()][]);
        }

        // should we sort arrays?
        Arrays.sort(intervals, (a, b) -> a[0] - b[0]);
        // list is seeding with empty value

        // should we add a seed of list?

        // compare the current item with the current loop
        // how to know the current in list => list.getLast()
        // how to know the current in loop => interval[i]

        // if overlap => merge them into one
        // overlap is last[1] > current[0] && last[0] < current[1]
        //
        // else if not overlap
        // add item like normal not modify on the last edit
        for (int i = 0; i < intervals.length; i++) {

            if (list.isEmpty()) {
                list.add(intervals[i]);
            } else {
                int[] currentItem = list.getLast();
                if (currentItem[1] >= intervals[i][0]) {
                    int min = currentItem[0];
                    int max = Math.max(intervals[i][1], currentItem[1]);
                    // edit the current
                    list.set(list.size() - 1, new int[]{min, max});
                } else {
                    // add new
                    list.add(intervals[i]);
                }
                // add after calculated and merged

            }

        }
        return list.toArray(new int[list.size()][]);
    }
}

class Solution {

    public int[][] merge(int[][] intervals) {
        ArrayList<int[]> ans = new ArrayList<>();
        Arrays.sort(intervals, (a, b) -> Integer.compare(a[0], b[0]));
        ans.add(intervals[0]);
        for (int i = 1; i < intervals.length; i++) {
            //comparing the values of prevEnd and curStart
            int curStart = intervals[i][0];
            if (curStart <= ans.get(ans.size() - 1)[1]) {
                //do the merging
                ans.get(ans.size() - 1)[1] =
                    Math.max(ans.get(ans.size() - 1)[1], intervals[i][1]);
            } else {
                ans.add(intervals[i]);
            }
        }
        int[][] res = new int[ans.size()][2];
        ans.toArray(res);
        return res;
    }
}
