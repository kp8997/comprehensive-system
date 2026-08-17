package dev.kp8997._07_tree._53_binary_tree_right_side_view;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> list = new ArrayList<Integer>();
        if (root == null) return list;
        bfs(list, root);
        return list;
    }

    public void bfs(List<Integer> list, TreeNode root) {
        Queue<TreeNode> q = new LinkedList<>();
        q.offer(root);
        while (!q.isEmpty()) {
            int levelSize = q.size();
            for (int i = 0; i < levelSize; i++) {
                TreeNode cur = q.poll();
                if (i == 0) list.add(cur.val);
                if (cur.right != null) q.offer(cur.right);
                if (cur.left != null) q.offer(cur.left);
            }
        }
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
