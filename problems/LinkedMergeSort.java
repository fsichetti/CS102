/*
 * Merge sort on a linked list
 * ---------------------------
 * Sort a singly linked list in O(n log n) time.
 *
 * On an array, merge copies elements into an auxiliary array: O(n) extra
 * memory. On a linked list, merge relinks the existing nodes: no copies,
 * O(1) extra memory for one merge.
 *
 * Top-down (recursive): split in the middle (slow/fast pointers, or count),
 * sort the two halves, merge. Extra memory: O(log n) for the recursion.
 *
 * Bottom-up (iterative): no recursion, O(1) extra memory.
 *   for width = 1, 2, 4, ...  while width < n:
 *     walk the list: cut off two runs of `width` nodes, merge them, append the
 *     result to the part already processed.
 * After the pass with width w, every run of 2w consecutive nodes is sorted.
 * log n passes, O(n) each.
 *
 * Merge takes from the left run when equal: stable.
 *
 * Quicksort's pivot choice (random or median of three) needs
 * random access, the partition itself can be done by relinking.
 * Insertion sort works on a linked list, O(n^2).
 * java.util.LinkedList.sort copies to an array, sorts, and copies back.
 */

import java.util.*;

public class LinkedMergeSort {

    static class Node {
        int data;
        Node next;

        Node(int data, Node next) {
            this.data = data;
            this.next = next;
        }
    }

    // ==== Merge ====

    // links the merge of the sorted lists a and b after tail, returns the last node
    static Node merge(Node a, Node b, Node tail) {
        while (a != null && b != null) {
            if (a.data <= b.data) {     // <=: stable
                tail.next = a;
                a = a.next;
            } else {
                tail.next = b;
                b = b.next;
            }
            tail = tail.next;
        }
        tail.next = (a != null) ? a : b;    // the rest is already linked
        while (tail.next != null) tail = tail.next;
        return tail;
    }

    // ==== Top-down ====

    static Node sortTopDown(Node head) {
        if (head == null || head.next == null) return head;

        Node slow = head, fast = head.next;     // slow ends at the last node of the first half
        while (fast != null && fast.next != null) {
            slow = slow.next;
            fast = fast.next.next;
        }
        Node second = slow.next;
        slow.next = null;

        Node dummy = new Node(0, null);         // placeholder before the first node
        merge(sortTopDown(head), sortTopDown(second), dummy);
        return dummy.next;
    }

    // ==== Bottom-up ====

    // cuts the list after k nodes, returns what follows (null if fewer than k+1 nodes)
    static Node cut(Node head, int k) {
        for (int i = 1; i < k && head != null; i++) head = head.next;
        if (head == null) return null;
        Node rest = head.next;
        head.next = null;
        return rest;
    }

    static Node sortBottomUp(Node head) {
        int n = 0;
        for (Node p = head; p != null; p = p.next) n++;

        Node dummy = new Node(0, head);         // dummy.next is always the current first node
        for (int width = 1; width < n; width *= 2) {
            Node tail = dummy;                  // last node of the processed part
            Node rest = dummy.next;
            while (rest != null) {
                Node left = rest;
                Node right = cut(left, width);
                rest = cut(right, width);
                tail = merge(left, right, tail);
            }
        }
        return dummy.next;
    }

    // ==== Demo ====

    static Node fromArray(int[] a) {
        Node head = null;
        for (int i = a.length - 1; i >= 0; i--) head = new Node(a[i], head);
        return head;
    }

    static String show(Node head) {
        StringBuilder sb = new StringBuilder("[");
        for (Node p = head; p != null; p = p.next) {
            sb.append(p.data);
            if (p.next != null) sb.append(", ");
        }
        return sb.append("]").toString();
    }

    public static void main(String[] args) {
        int[] a = {5, 2, 9, 1, 5, 6, 3, 8, 7};
        System.out.println(show(sortTopDown(fromArray(a))));
        System.out.println(show(sortBottomUp(fromArray(a))));
        System.out.println(show(sortBottomUp(fromArray(new int[]{}))));
        System.out.println(show(sortBottomUp(fromArray(new int[]{42}))));

        Random rnd = new Random(102);
        for (int t = 0; t < 1000; t++) {
            int[] r = new int[rnd.nextInt(50)];
            for (int i = 0; i < r.length; i++) r[i] = rnd.nextInt(20);
            int[] sorted = r.clone();
            Arrays.sort(sorted);
            String expected = Arrays.toString(sorted);
            if (!show(sortBottomUp(fromArray(r))).equals(expected)
                    || !show(sortTopDown(fromArray(r))).equals(expected))
                throw new AssertionError(Arrays.toString(r));
        }
        System.out.println("random test passed");

        int n = 1_000_000;
        int[] big = new int[n];
        for (int i = 0; i < n; i++) big[i] = rnd.nextInt();
        long t0 = System.nanoTime();
        sortBottomUp(fromArray(big));
        System.out.println("bottom-up, n = " + n + ": " + (System.nanoTime() - t0) / 1_000_000 + " ms");
    }
}
