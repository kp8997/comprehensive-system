package dev.kp8997._07_tree._59_serialize_and_deserialize_binary_tree;

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
    TreeNode() {}
    TreeNode(int val) { this.val = val; }
    TreeNode(int val, TreeNode left, TreeNode right) {
        this.val = val;
        this.left = left;
        this.right = right;
    }
}

class Codec {

    private int i;

    // Encodes a tree to a single string.
    public String serialize(TreeNode root) {
        List<String> list = new ArrayList<>();
        serializeDFS(root, list);

        return String.join(",", list);
    }

    private void serializeDFS(TreeNode root, List<String> list) {
        if (root == null) {
            list.add("N");
            return;
        }
        list.add(String.valueOf(root.val));
        serializeDFS(root.left, list);
        serializeDFS(root.right, list);
    }

    // Decodes your encoded data to tree.
    public TreeNode deserialize(String data) {
        String[] tokens = data.split(",");
        return deserializeDFS(tokens);
    }

    private TreeNode deserializeDFS(String[] tokens) {
        String token = tokens[this.i];
        if (token.equals("N")) {
            this.i++;
            return null;
        }
        var node = new TreeNode(Integer.parseInt(token));
        this.i++;
        node.left = deserializeDFS(tokens);
        node.right = deserializeDFS(tokens);
        return node;
    }
}
// Your Codec object will be instantiated and called as such:
// Codec ser = new Codec();
// Codec deser = new Codec();
// TreeNode ans = deser.deserialize(ser.serialize(root));

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
