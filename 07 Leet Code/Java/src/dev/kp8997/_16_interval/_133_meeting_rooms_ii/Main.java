package dev.kp8997._16_interval._133_meeting_rooms_ii;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        List<Interval> intervals = new ArrayList<>();
        intervals.add(new Interval(1, 4));
        intervals.add(new Interval(2, 8));
        intervals.add(new Interval(3, 5));
        intervals.add(new Interval(5, 7));
        intervals.add(new Interval(5, 9));
        intervals.add(new Interval(8, 12));
        intervals.add(new Interval(10, 15));
        System.out.println(new Solution().minMeetingRooms(intervals));
    }
}

/**
 * Definition of Interval:
 * public class Interval {
 * int start, end;
 * Interval(int start, int end) {
 * this.start = start;
 * this.end = end;
 * }
 * }
 */

class Interval {
    int start, end;

    Interval(int start, int end) {
        this.start = start;
        this.end = end;
    }
}

class Solution {
    public int minMeetingRooms(List<Interval> intervals) {
        //intervals.sort(Comparator.comparingInt(a -> a.start));
        int n = intervals.size();
        int[] startTime = new int[n];
        int[] endTimes = new int[n];

        for (int i = 0; i < n; i++) {
            startTime[i] = intervals.get(i).start;
            endTimes[i] = intervals.get(i).end;
        }
        Arrays.sort(startTime);
        Arrays.sort(endTimes);
        int l = 0;
        int r = 0;
        int room = 0;
        while (l < n) {
            if (startTime[l] < endTimes[r]) {
                room++;
            } else {
                r++;
            }
            l++;
        }

        return room;
    }
}

class Solution1 {

    /**
     * @param intervals: an array of meeting time intervals
     * @return: the minimum number of conference rooms required
     */
    public int minMeetingRooms(List<Interval> intervals) {
        if (intervals.isEmpty()) return 0;

        Collections.sort(
                intervals,
                (a, b) -> Integer.compare(a.start, b.start)
        );

        Queue<Interval> queue = new PriorityQueue<>((a, b) ->
                Integer.compare(a.end, b.end)
        );

        int count = 0;
        for (Interval interval : intervals) {
            while (
                    !queue.isEmpty() && interval.start >= queue.peek().end
            ) queue.poll();

            queue.offer(interval);
            count = Math.max(count, queue.size());
        }
        return count;
    }
}

// Two pointer approach
class Solution2 {

    public int minMeetingRooms(List<Interval> intervals) {
        if (intervals.size() == 0) {
            return 0;
        }

        int len = intervals.size();
        int[] startTime = new int[len];
        int[] endTime = new int[len];

        for (int i = 0; i < len; i++) {
            startTime[i] = intervals.get(i).start;
            endTime[i] = intervals.get(i).end;
        }

        Arrays.sort(startTime);
        Arrays.sort(endTime);

        int res = 0;
        int count = 0;
        int s = 0;
        int e = 0;

        while (s < len) {
            if (startTime[s] < endTime[e]) {
                s++;
                count++;
            } else {
                e++;
                count--;
            }
            res = Math.max(res, count);
        }

        return res;
    }
}
