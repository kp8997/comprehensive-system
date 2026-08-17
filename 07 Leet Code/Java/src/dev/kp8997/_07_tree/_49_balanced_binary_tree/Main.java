package dev.kp8997._07_tree._49_balanced_binary_tree;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class BalancedBinaryTree {

    private static Pair<Boolean, Integer> dfs(TreeNode root) {
        if (root == null) {
            return new Pair<Boolean, Integer>(true, 0);
        }

        var left = dfs(root.left);
        var right = dfs(root.right);

        var balanced =
            left.getKey() &&
            right.getKey() &&
            (Math.abs(left.getValue() - right.getValue()) <= 1);

        return new Pair<Boolean, Integer>(
            balanced,
            1 + Math.max(left.getValue(), right.getValue())
        );
    }

    public static boolean isBalanced(TreeNode root) {
        return dfs(root).getKey();
    }
}

// Solution using the bottom up approach
// TC and SC is On

class Solution {

    public int height(TreeNode root){
        if(root == null){
            return 0;
        }

        int lh = height(root.left);
        int rh = height(root.right);

        return 1 + Math.max(lh,rh);
    }

    public boolean isBalanced(TreeNode root) {

        if(root == null){
            return true;
        }

        int lh = height(root.left);
        int rh = height(root.right);

        return Math.abs(lh - rh) <= 1 && isBalanced(root.left) && isBalanced(root.right);
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


class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return key;
    }

    public V getValue() {
        return value;
    }
}
