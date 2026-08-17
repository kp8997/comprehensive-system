package dev.kp8997._11_graph._81_clone_graph;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        
    }
}

class Solution {

    public HashMap<Integer, Node> map = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (map.containsKey(node.val)) return map.get(node.val);
        Node newNode = new Node(node.val, new ArrayList<Node>());
        map.put(node.val, newNode);
        for (Node neighbor : node.neighbors) {
            newNode.neighbors.add(cloneGraph(neighbor));
        }
        return newNode;
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
