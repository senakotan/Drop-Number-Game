package com.mycompany.dropnumbergame;

public class Node {

    int value;
    Node right;
    Node down;  

    public Node(int value) {
        this.value = value;
        this.right = null;
        this.down = null;
    }

}
