package dev.kp8997._16_interval._132_meeting_rooms;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

/**
 * Definition of Interval:
 * public class Interval {
 *     int start, end;
 *     Interval(int start, int end) {
 *         this.start = start;
 *         this.end = end;
 *     }
 * }
 */

class Interval {
     int start, end;
     Interval(int start, int end) {
         this.start = start;
         this.end = end;
     }
}

class Solution2 {
    public boolean canAttendMeetings(List<Interval> intervals) {
        Collections.sort(intervals, Comparator.comparing((Interval a) -> a.start));
        for (int i = 0; i < intervals.size() - 1;i++ ) {
            if (intervals.get(i).end > intervals.get(i + 1).start) {
                return false;
            }
        }

        return true;
    }
}

class Solution {
    public boolean canAttendMeetings(List<Interval> intervals) {
        int length = intervals.size();
        if (intervals.size() == 0 || length == 1) return true;
        // Write your code here

        int[] start = new int[length];
        int[] end = new int[length];
        for (int i = 0; i < length; i++) {
            start[i] = intervals.get(i).start;
            end[i] = intervals.get(i).end;
        }

        Arrays.sort(start);
        Arrays.sort(end);

        int j = 0;
        while (j + 1 < length) {
            if (end[j] > start[j + 1]) return false;
            j++;
        }

        return true;
    }
}

class Solution1 {

    public boolean canAttendMeetings(List<Interval> intervals) {
        Collections.sort(intervals, (a, b) -> a.start - b.start);

        for (int i = 0; i + 1 < intervals.size(); i++) {
            if (intervals.get(i).end > intervals.get(i + 1).start) {
                return false;
            }
        }
        return true;
    }
}
