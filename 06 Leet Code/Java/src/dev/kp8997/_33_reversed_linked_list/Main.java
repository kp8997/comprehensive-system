package dev.kp8997._33_reversed_linked_list;

public class Main {
    static void main() {System.out.println(123);
    }
}

class ListNode {
    int val;
    ListNode next;
    ListNode() {}
    ListNode(int val) { this.val = val; }
    ListNode(int val, ListNode next) { this.val = val; this.next = next; }
}

class Solution {
    public ListNode reverseList(ListNode head) {
        ListNode rv = null;
        ListNode cur = head;
        while (cur != null) {
            ListNode next = cur.next;
            cur.next = rv;
            rv = cur;
            cur = next;
        }
        return rv;
    }
}
