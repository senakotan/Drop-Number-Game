
package com.mycompany.dropnumbergame;


import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.Stack;

public class GameGUI extends Application {

    private MultiLinkedList gameEngine = new MultiLinkedList();

    private GridPane gridDisplay = new GridPane();

    private Stack<MultiLinkedList> undoStack = new Stack<>();
    private Stack<Integer> scoreUndoStack = new Stack<>();

    private boolean gameOver = false;

    private final int ROWS = 7;
    private final int COLS = 5;

    private final int MERGE_DELAY = 500;

    private int scenarioIndex = 0;

    private boolean isProcessing = false;

    private int currentScore = 0;
    private int bestScore = 4392;

    private Text scoreVal;
    private Text bestScoreVal;

    private final int[][] scenario = {
        {2, 0}, {2, 3}, {4, 1}, {2, 2}, {4, 4},
        {2, 1}, {4, 4}, {8, 0}, {8, 0}, {32, 1},
        {2, 2}, {64, 2},
        {16, 3}, {64, 1}, {32, 2}, {16, 0}, {16, 4},
        {32, 2}, {64, 1}, {8, 3}, {4, 3}, {2, 3},
        {2, 3}, {2, 1},
        {64, 2}, {32, 2}, {16, 2}, {8, 2},
        {8, 2}, {4, 1}, {8, 1}
    };

    @Override
    public void start(Stage primaryStage) {
        VBox root = new VBox(15);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #faf8ef;");
        root.setAlignment(Pos.TOP_CENTER);

        HBox topRow = new HBox();
        topRow.setAlignment(Pos.CENTER_LEFT);
        topRow.setSpacing(80);

        Text title = new Text("2048");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 80));
        title.setFill(Color.web("#776e65"));

        HBox scoresContainer = new HBox(10);
        VBox scoreBox = createScoreBox("PUAN", "0");
        scoreVal = (Text) scoreBox.getChildren().get(1);

        VBox bestBox = createScoreBox("EN İYİ", String.valueOf(bestScore));
        bestScoreVal = (Text) bestBox.getChildren().get(1);

        scoresContainer.getChildren().addAll(scoreBox, bestBox);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        topRow.getChildren().addAll(title, spacer, scoresContainer);

        HBox middleRow = new HBox();
        middleRow.setAlignment(Pos.CENTER_LEFT);

        VBox subTitleArea = new VBox(5);
        Text sub1 = new Text("2048 Oyun Oyna Online");
        sub1.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        sub1.setFill(Color.web("#776e65"));

        Text sub2 = new Text("Sayıları katlayın ve 2048 karo olsun!");
        sub2.setFont(Font.font("Arial", 16));
        sub2.setFill(Color.web("#776e65"));

        subTitleArea.getChildren().addAll(sub1, sub2);

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button startBtn = new Button("Yeni Oyun");
        startBtn.setStyle("-fx-background-color: #8f7a66; -fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 18; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
        startBtn.setFocusTraversable(false);
        startBtn.setOnAction(e -> restartGame());

        middleRow.getChildren().addAll(subTitleArea, spacer2, startBtn);

        gridDisplay.setHgap(10);
        gridDisplay.setVgap(10);
        gridDisplay.setPadding(new Insets(15));
        gridDisplay.setStyle("-fx-background-color: #bbada0; -fx-background-radius: 6;");
        gridDisplay.setAlignment(Pos.CENTER);

        StackPane gameContainer = new StackPane();
        gameContainer.getChildren().add(gridDisplay);

        refreshUI();

        root.getChildren().addAll(topRow, middleRow, gameContainer);

        Scene scene = new Scene(root, 600, 900);

        scene.setOnKeyPressed(event -> {
            if (isProcessing) {
                return;
            }

            switch (event.getCode()) {
                case RIGHT:
                    nextStep();
                    break;
                case LEFT:
                    undoStep();
                    break;
            }
        });

        primaryStage.setTitle("Drop Number Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        root.requestFocus();
    }

    private VBox createScoreBox(String label, String value) {
        VBox box = new VBox(2);
        box.setMinWidth(100);
        box.setPadding(new Insets(5, 10, 5, 10));
        box.setStyle("-fx-background-color: #bbada0; -fx-background-radius: 5;");
        box.setAlignment(Pos.CENTER);

        Text lbl = new Text(label);
        lbl.setFill(Color.web("#eee4da"));
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        Text val = new Text(value);
        val.setFill(Color.WHITE);
        val.setFont(Font.font("Arial", FontWeight.BOLD, 22));

        box.getChildren().addAll(lbl, val);
        return box;
    }

    private void restartGame() {
        gameEngine = new MultiLinkedList();
        undoStack.clear();
        scoreUndoStack.clear();
        scenarioIndex = 0;
        currentScore = 0;
        isProcessing = false;
        gameOver = false;

        Platform.runLater(() -> {
            StackPane parent = (StackPane) gridDisplay.getParent();

            if (parent.getChildren().size() > 1) {
                parent.getChildren().remove(1, parent.getChildren().size());
            }

            gridDisplay.getChildren().clear();
            refreshUI();
            gridDisplay.requestFocus();
        });
    }

    private void nextStep() {
        if (isProcessing || gameOver) {
            return;
        }

        if (scenarioIndex >= scenario.length) {
            return;
        }

        undoStack.push(gameEngine.deepCopy());
        scoreUndoStack.push(currentScore);

        isProcessing = true;

        int val = scenario[scenarioIndex][0];
        int col = scenario[scenarioIndex][1];

        boolean dropped = gameEngine.dropNumberOnly(val, col);

        if (!dropped) {
            isProcessing = false;
            showGameOver();
            return;
        }

        animateTileFall(col);

        scenarioIndex++;

        new Thread(() -> {
            try {
                Thread.sleep(MERGE_DELAY + 100);

                while (gameEngine.triggerMerge(col)) {
                    Node headerNode = gameEngine.getColumnHeader(col);
                    Node mergedNode = headerNode.down;

                    if (mergedNode != null) {
                        currentScore += mergedNode.value;
                    }

                    Platform.runLater(this::refreshUI);
                    Thread.sleep(MERGE_DELAY);
                }

                Platform.runLater(() -> {
                    refreshUI();

                    if (gameEngine.isAnyColumnFull()) {
                        showGameOver();
                    }

                    isProcessing = false;
                });

            } catch (InterruptedException e) {
                e.printStackTrace();
                isProcessing = false;
            }
        }).start();
    }

    private void undoStep() {
        if (isProcessing) {
            return;
        }

        if (undoStack.isEmpty() || scoreUndoStack.isEmpty() || scenarioIndex <= 0) {
            return;
        }

        gameEngine = undoStack.pop();
        currentScore = scoreUndoStack.pop();
        scenarioIndex--;

        gameOver = false;

        refreshUI();
    }

    private void animateTileFall(int colIndex) {
        refreshUI();

        int nodesInCol = gameEngine.countNodes(gameEngine.getColumnHeader(colIndex));
        int rowInGrid = ROWS - nodesInCol;

        gridDisplay.getChildren().stream()
                .filter(node -> GridPane.getColumnIndex(node) == colIndex && GridPane.getRowIndex(node) == rowInGrid)
                .findFirst()
                .ifPresent(node -> {
                    TranslateTransition tt = new TranslateTransition(Duration.millis(300), node);
                    tt.setFromY(-500);
                    tt.setToY(0);
                    tt.play();
                });
    }

    private void showGameOver() {
        if (gameOver) {
            return;
        }

        gameOver = true;

        StackPane overlay = new StackPane();
        overlay.setStyle("-fx-background-color: rgba(238, 228, 218, 0.73); -fx-background-radius: 6;");
        overlay.setMaxSize(gridDisplay.getBoundsInParent().getWidth(), gridDisplay.getBoundsInParent().getHeight());

        VBox msgBox = new VBox(20);
        msgBox.setAlignment(Pos.CENTER);

        Text t = new Text("Oyun Bitti!");
        t.setFont(Font.font("Arial", FontWeight.BOLD, 60));
        t.setFill(Color.web("#776e65"));

        Button rbtn = new Button("Tekrar Dene");
        rbtn.setStyle("-fx-background-color: #8f7a66; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 10 20; -fx-cursor: hand;");
        rbtn.setOnAction(e -> restartGame());

        msgBox.getChildren().addAll(t, rbtn);
        overlay.getChildren().add(msgBox);

        StackPane parent = (StackPane) gridDisplay.getParent();
        parent.getChildren().add(overlay);
    }

    private void refreshUI() {
        gridDisplay.getChildren().clear();

        scoreVal.setText(String.valueOf(currentScore));

        if (currentScore > bestScore) {
            bestScore = currentScore;
            bestScoreVal.setText(String.valueOf(bestScore));
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                Node headerNode = gameEngine.getColumnHeader(j);
                int nodesInCol = gameEngine.countNodes(headerNode);
                int emptySpaces = ROWS - nodesInCol;

                StackPane cell = new StackPane();
                cell.setPrefSize(80, 80);

                if (i >= emptySpaces) {
                    Node target = gameEngine.getNodeFromBottom(headerNode, (ROWS - 1) - i);

                    if (target != null) {
                        int val = target.value;
                        cell.setStyle("-fx-background-color: " + getTileColor(val) + "; -fx-background-radius: 5;");

                        Text text = new Text(String.valueOf(val));
                        text.setFont(Font.font("Arial", FontWeight.BOLD, 30));
                        text.setFill(val <= 4 ? Color.web("#776e65") : Color.WHITE);
                        cell.getChildren().add(text);
                    } else {
                        cell.setStyle("-fx-background-color: rgba(205, 193, 180, 0.35); -fx-background-radius: 5;");
                    }
                } else {
                    cell.setStyle("-fx-background-color: rgba(205, 193, 180, 0.35); -fx-background-radius: 5;");
                }

                gridDisplay.add(cell, j, i);
            }
        }
    }

    private String getTileColor(int value) {
        switch (value) {
            case 2:
                return "#eee4da";
            case 4:
                return "#ede0c8";
            case 8:
                return "#f2b179";
            case 16:
                return "#f59563";
            case 32:
                return "#f67c5f";
            case 64:
                return "#f65e3b";
            case 128:
                return "#edcf72";
            case 256:
                return "#edcc61";
            case 512:
                return "#edc850";
            default:
                return "#3c3a32";
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}