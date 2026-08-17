package dev.kp8997._07_tree._54_count_good_nodes_in_binary_tree;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public int goodNodes(TreeNode root) {
        return helper(root, -99999);
    }

    public int helper(TreeNode root, int max) {
        if (root == null) return 0;

        int res = root.val >= max ? 1 : 0;

        res += helper(root.left, Math.max(root.val, max));
        res += helper(root.right, Math.max(root.val, max));

        return res;
    }
}

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
