package com.mycompany.dropnumbergame;


public class MultiLinkedList {

    Node head;
    private final int MAX_ROWS = 7;
    private final int MAX_COLS = 5;

    public MultiLinkedList() {
        head = new Node(-1);

        Node temp = head;

        for (int i = 1; i < MAX_COLS; i++) {
            temp.right = new Node(-1);
            temp = temp.right;
        }
    }

    public Node getColumnHeader(int colIndex) {
        if (colIndex < 0 || colIndex >= MAX_COLS) {
            return null;
        }

        Node temp = head;
        for (int i = 0; i < colIndex; i++) {
            temp = temp.right;
        }

        return temp;
    }

    public boolean dropNumberOnly(int value, int colIndex) {
        Node colHeader = getColumnHeader(colIndex);

        if (colHeader == null) {
            return false;
        }

        if (countNodes(colHeader) >= MAX_ROWS) {
            return false;
        }

        Node newNode = new Node(value);

        newNode.down = colHeader.down;
        colHeader.down = newNode;

        return true;
    }

    public boolean isAnyColumnFull() {
        for (int i = 0; i < MAX_COLS; i++) {
            Node colHeader = getColumnHeader(i);

            if (countNodes(colHeader) >= MAX_ROWS) {
                return true;
            }
        }

        return false;
    }

    public boolean triggerMerge(int colIndex) {
        Node colHeader = getColumnHeader(colIndex);

        if (colHeader == null) {
            return false;
        }

        if (colHeader.down == null || colHeader.down.down == null) {
            return false;
        }

        Node top = colHeader.down;
        Node second = top.down;

        if (top.value == second.value) {
            second.value *= 2;
            colHeader.down = second;
            return true;
        }

        return false;
    }

    public int countNodes(Node h) {
        if (h == null) {
            return 0;
        }

        int count = 0;

        Node temp = h.down;
        while (temp != null) {
            count++;
            temp = temp.down;
        }

        return count;
    }

    public MultiLinkedList deepCopy() {
        MultiLinkedList newCopy = new MultiLinkedList();

        for (int i = 0; i < MAX_COLS; i++) {
            Node originalHeader = this.getColumnHeader(i);
            copyColumnRecursively(originalHeader.down, newCopy, i);
        }

        return newCopy;
    }

    private void copyColumnRecursively(Node node, MultiLinkedList newCopy, int colIndex) {
        if (node == null) {
            return;
        }

        copyColumnRecursively(node.down, newCopy, colIndex);

        newCopy.dropNumberOnly(node.value, colIndex);
    }

    public Node getNodeFromBottom(Node h, int indexFromBottom) {
        if (h == null) {
            return null;
        }

        int total = countNodes(h);

        if (indexFromBottom < 0 || indexFromBottom >= total) {
            return null;
        }

        int indexFromTop = (total - 1) - indexFromBottom;

        Node temp = h.down;
        for (int i = 0; i < indexFromTop; i++) {
            temp = temp.down;
        }

        return temp;
    }
}
