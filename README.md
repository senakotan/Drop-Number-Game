# DropNumberGame-MultiLinkedList

## Overview

This project is a custom 2048-inspired number merging game implemented using a **Multi Linked List** data structure instead of a traditional 2D array. The game demonstrates how a two-dimensional board can be represented through linked structures while supporting insertion, merging, undo operations, and graphical visualization.

The application was developed in **Java** using **JavaFX** for the graphical user interface.

## Features

* Multi Linked List based board representation
* Column-based number insertion
* Automatic merge operations
* Undo functionality using stack snapshots
* Animated tile dropping
* Score and best score tracking
* Game Over detection
* JavaFX graphical interface

## Data Structure Design

The board consists of:

* Horizontal links connecting column headers
* Vertical links connecting nodes within each column
* Add-first insertion strategy
* Recursive deep copy mechanism for undo support

## Project Structure

```text
src/
├── Node.java
├── MultiLinkedList.java
├── GameGUI.java
```

## Algorithms

* Tile Insertion
* Merge Operation
* Deep Copy
* Undo Mechanism
* Board Refresh and Visualization

## Technologies

* Java
* JavaFX
* Multi Linked List
* Stack


