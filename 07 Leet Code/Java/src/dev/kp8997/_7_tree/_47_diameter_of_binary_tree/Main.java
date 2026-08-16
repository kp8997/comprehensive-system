package dev.kp8997._7_tree._47_diameter_of_binary_tree;

public class Main {
    static void main() {

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
    //private int maxDepth(TreeNode root) {
    //    if (root != null) {
    //        int leftDepth = maxDepth(root.left);
    //        int rightDepth = maxDepth(root.right);
    //        return Math.max(leftDepth, rightDepth) + 1;
    //    }
    //
    //    return 0;
    //}

    private int maxDiameter = 0;

    public int diameterOfBinaryTree(TreeNode root) {
        maxDiameter = 0;
        calculateHeight(root);
        return maxDiameter;
    }

    private int calculateHeight(TreeNode root) {
        if (root != null) {
            int leftDepth = calculateHeight(root.left);
            int rightDepth = calculateHeight(root.right);
            int currentDia = leftDepth + rightDepth;
            maxDiameter = Math.max(maxDiameter, currentDia);
            return Math.max(leftDepth, rightDepth) + 1;
        }

        return 0;
    }
}