package com.mycompany.dropnumbergame;

public class Node {

    int value;
    Node right; // sağ sütunun başına gider
    Node down;  // aynı sütunda aşağı iner

    public Node(int value) {
        this.value = value;
        this.right = null;
        this.down = null;
    }

}
