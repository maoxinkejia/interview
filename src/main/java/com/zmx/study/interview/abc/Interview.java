package com.zmx.study.interview.abc;

import java.util.List;

/**
 * /*
 *   public class ListNode {
 *       int val;
 *       ListNode next;
 *       ListNode(int x) { val = x; }
 *   }
 *
 *输入：(4 -> 3 -> 2) + (6 -> 3 -> 8 -> 9 -> 9 -> 9)
         *输出：0 -> 7 -> 4 -> 1
         *原理：234 + 1236 = 1470
 */
public class Interview {
    public static void main(String[] args) {
        ListNode l1 = new ListNode(4);
        l1.next = new ListNode(3);
        l1.next.next = new ListNode(2);

        ListNode l2 = new ListNode(6);
        l2.next = new ListNode(3);
        l2.next.next = new ListNode(2);
        l2.next.next.next = new ListNode(1);
        ListNode add = add(l1, l2);
        System.out.println(add);
    }

    public static ListNode add(ListNode l1, ListNode l2) {
        if (l1 == null) {
            return l2;
        }
        if (l2 == null) {
            return l1;
        }

        ListNode first = new ListNode(-1);
        ListNode head = first;
        int num = 0;
        int sum = 0;
        while (l1 != null || l2 != null || num > 0) {
            if (l1 != null) {
                sum += l1.val;
                l1 = l1.next;
            }

            if (l2 != null) {
                sum += l2.val;
                l2 = l2.next;
            }

            sum += num;

            head.next = new ListNode(sum % 10);
            head = head.next;
            num = sum / 10;
            sum = 0;
        }

        return first.next;
    }
}


class ListNode {
    int val;
    ListNode next;

    ListNode(int x) {
        val = x;
    }

    @Override
    public String toString() {
        return "ListNode{" +
                "val=" + val +
                ", next=" + next +
                '}';
    }
}