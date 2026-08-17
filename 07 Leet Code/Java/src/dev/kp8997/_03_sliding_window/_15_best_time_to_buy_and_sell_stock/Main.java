package dev.kp8997._3_sliding_window._15_best_time_to_buy_and_sell_stock;

public class Main {
    static void main() {

    }
}

class Solution {

    //Greedy (One Pass)
    public static int maxProfit(int[] prices) {
        int minPrice = Integer.MAX_VALUE;
        int maxProfit = 0;

        for (int price : prices) {
            if (price < minPrice) {
                minPrice = price; // Update lowest buying point
            } else if (price - minPrice > maxProfit) {
                maxProfit = price - minPrice; // Update highest profit seen so far
            }
        }

        return maxProfit;
    }

    public static int maxProfit1(int[] prices) {
        int bestProfit = 0;
        int left = 0;
        int right = 1;

        while (right < prices.length) {
            if (prices[left] < prices[right]) {
                int currentProfit = prices[right] - prices[left];
                if (currentProfit > bestProfit) {
                    bestProfit = currentProfit;
                }
            } else {
                left = right;
            }

            right++;
        }

        return bestProfit;
    }

    public static int maxProfit2(int[] prices) {
        int len = prices.length;
        int bestProfit = 0;

        for (int i = 0; i < len; i++) {
            for (int j = i + 1; j < len; j++) {
                int tempProfit = prices[j] - prices[i];
                if (prices[j] > prices[i] && tempProfit > bestProfit) {
                    bestProfit = tempProfit;
                }
            }
        }

        return bestProfit;
    }
}