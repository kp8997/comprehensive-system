package dev.kp8997._06_encode_decode;

import java.util.ArrayList;
import java.util.List;

public class Main {
    static void main() {
        List<String> ls = new ArrayList<>(List.of("abc", "xyz", "rbk"));
        var ts = Solution.encode(ls);
        System.out.println(ts);

        var rs = Solution.decode(ts);
        System.out.println(rs);
    }
}


class Solution {
    public static String encode(List<String> strs) {
        StringBuilder encoded = new StringBuilder();

        for (String s : strs) {
            // Format: <length>#<payload>
            encoded.append(s.length()).append('#').append(s);
        }

        return encoded.toString();
    }

    // Decodes a single string to a list of strings.
    public static List<String> decode(String s) {
        List<String> result = new ArrayList<>();
        int i = 0;
        int n = s.length();

        while (i < n) {
            // Find the delimiter index
            int delimiterIndex = s.indexOf('#', i);

            // Extract and parse the length of the payload
            int length = Integer.parseInt(s.substring(i, delimiterIndex));

            // Extract the actual string payload
            int start = delimiterIndex + 1;
            int end = start + length;
            result.add(s.substring(start, end));

            // Advance the index past the extracted payload segment
            i = end;
        }

        return result;
    }}
