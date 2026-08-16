package dev.kp8997._6_linked_list._34_merge_2_sorted_list;


public class Main {
    static void main() {

    }
}

/**
 * Definition for singly-linked list.
 */
class ListNode {
    int val;
    ListNode next;

    ListNode() {
    }

    ListNode(int val) {
        this.val = val;
    }

    ListNode(int val, ListNode next) {
        this.val = val;
        this.next = next;
    }
}


class Solution {
    public ListNode mergeTwoLists(ListNode list1, ListNode list2) {
        ListNode head = null;
        ListNode prev = null;

        while (list1 != null && list2 != null) {
            ListNode cur;
            if (list1.val <= list2.val) {
                cur = list1;
                list1 = list1.next;
                ;
            } else {
                cur = list2;
                list2 = list2.next;
            }

            if (prev == null) {
                prev = cur;
                head = cur;
            } else {
                prev.next = cur;
                prev = prev.next;
            }
        }

        if (list1 != null) {
            if (prev != null) prev.next = list1;
            else head = list1; // Handling edge case where one list was empty from start
        } else if (list2 != null) {
            if (prev != null) prev.next = list2;
            else head = list2;
        }
        return head;
    }
}
