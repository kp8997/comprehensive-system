package dev.kp8997._09_backtracking._68_combination_sum;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<List<Integer>> ans = new ArrayList<List<Integer>>();
        List<Integer> cur = new ArrayList();
        backtrack(candidates, target, ans, cur, 0);
        return ans;
    }

    public void backtrack(
        int[] candidates,
        int target,
        List<List<Integer>> ans,
        List<Integer> cur,
        int index
    ) {
        if (target == 0) {
            ans.add(new ArrayList(cur));
        } else if (target < 0 || index >= candidates.length) {
            return;
        } else {
            cur.add(candidates[index]);
            backtrack(candidates, target - candidates[index], ans, cur, index);

            cur.remove(cur.get(cur.size() - 1));
            backtrack(candidates, target, ans, cur, index + 1);
        }
    }
}
