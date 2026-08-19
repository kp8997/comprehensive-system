package dev.kp8997._16_interval._131_non_overlapping_intervals;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        int[][] intervals = {{1,2},{2,3},{3,4},{1,3}};
        System.out.println(new Solution1().eraseOverlapIntervals(intervals));

        int[][] intervals1 = {{1,2},{1,2},{1,2}};
        System.out.println(new Solution1().eraseOverlapIntervals(intervals1));
    }
}

class Solution1 {
    public int eraseOverlapIntervals(int[][] intervals) {
        if (intervals == null || intervals.length == 0) {
            return 0;
        }
        // should we sort by endtime?
        Arrays.sort(intervals, Comparator.comparingInt(o -> o[1]));
        int numberOfRemoval = 0;

        // not init with lastEnd = 0 because we can have negative value
        int lastEnd = intervals[0][1];
        for (int i = 1; i < intervals.length; i++) {

                if (intervals[i][0] < lastEnd) {
                    // overlap => not choose current end
                    numberOfRemoval++;
                } else {
                    //non-overlap => reassign seed
                    lastEnd = intervals[i][1];
                }
            // purpose is choose the one with the earliest finishing, set the range from the end to end of array
                // save lastend first
                // then calculate base on the range from that to the end of the array
            // continue with it until hit overlap
                // overlap if new start < last end
                    // remove the interval of the current (new start) because after sorted, end of new start > lastEnd
                    // increase counter with 1
            // not overlap => good to loop
        }
        return numberOfRemoval;
    }
}

class Solution {

    public int eraseOverlapIntervals(int[][] intervals) {
        int intervalsRemoved = 0;

        Arrays.sort(
            intervals,
            (arr1, arr2) -> Integer.compare(arr1[0], arr2[0])
        );

        int[] intervalFirst = intervals[0];

        for (int i = 1; i < intervals.length; i++) {
            if (firstIntervalwithinSecond(intervalFirst, intervals[i])) {
                //mark first interval to be removed
                intervalsRemoved++;
                // determine which interval to remove
                //remove the interval that ends last
                if (intervalFirst[1] > intervals[i][1]) {
                    intervalFirst = intervals[i];
                }
            } else {
                intervalFirst = intervals[i];
            }
        }
        return intervalsRemoved;
    }

    public boolean firstIntervalwithinSecond(
        int[] intervalFirst,
        int[] intervalSecond
    ) {
        return intervalSecond[0] < intervalFirst[1];
    }
}
