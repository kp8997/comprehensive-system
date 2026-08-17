package dev.kp8997._07_tree._51_lowest_common_ancestor_of_a_binary_search_tree;

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
 *     TreeNode(int x) { val = x; }
 * }
 */

class TreeNode {
     int val;
     TreeNode left;
     TreeNode right;
     TreeNode(int x) { val = x; }
 }

class Solution {

    public TreeNode lowestCommonAncestor(TreeNode root, TreeNode p, TreeNode q) {
        if (root == null) {
            return null;
        }

        if (p.val == root.val || q.val == root.val) {
            return root;
        }

        if (p.val < root.val && q.val < root.val) {
            return lowestCommonAncestor(root.left, p, q);
        }
        
        if (p.val > root.val && q.val > root.val) {
            return lowestCommonAncestor(root.right, p, q);
        } 
        
        return root;
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
