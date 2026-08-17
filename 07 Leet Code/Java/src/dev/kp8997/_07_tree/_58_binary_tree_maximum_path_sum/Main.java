package dev.kp8997._07_tree._58_binary_tree_maximum_path_sum;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

/**
 * Definition for a binary tree node.
 * public class TreeNode {
 *     int val;
 *     TreeNode left;
 *     TreeNode right;
 *     TreeNode() {}
 *     TreeNode(int val) { this.val = val; }
 *     TreeNode(int val, TreeNode left, TreeNode right) {
 *         this.val = val;
 *         this.left = left;
 *         this.right = right;
 *     }
 * }
 */

class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode() {}
     TreeNode(int val) { this.val = val; }
     TreeNode(int val, TreeNode left, TreeNode right) {
         this.val = val;
         this.left = left;
         this.right = right;
     }
 }

class Solution {

    public int maxPathSum(TreeNode root) {
        int[] res = { Integer.MIN_VALUE };
        maxPathSum(root, res);
        return res[0];
    }

    public int maxPathSum(TreeNode root, int[] res) {
        if (root == null) return 0;

        int left = Math.max(0, maxPathSum(root.left, res));
        int right = Math.max(0, maxPathSum(root.right, res));
        res[0] = Math.max(res[0], root.val + left + right);

        return root.val + Math.max(left, right);
    }
}

class Node {
    public int val;
    public java.util.List<Node> neighbors;
    public Node() {
        val = 0;
        neighbors = new java.util.ArrayList<Node>();
    }
    public Node(int _val) {
        val = _val;
        neighbors = new java.util.ArrayList<Node>();
    }
    public Node(int _val, java.util.ArrayList<Node> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
}
