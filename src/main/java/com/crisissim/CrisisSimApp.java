package com.crisissim;

import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class CrisisSimApp extends Application {

    private static MainController controller;

    @Override
    public void start(Stage primaryStage) {
        controller = new MainController();
        Scene scene = new Scene(controller.getView());

        // Get screen dimensions for responsive sizing
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();

        // Set window to 85% of screen size
        double windowWidth = screenBounds.getWidth() * 0.85;
        double windowHeight = screenBounds.getHeight() * 0.85;

        primaryStage.setTitle("CrisisSim - Business Crisis Management Simulator");
        primaryStage.setScene(scene);

        // Allow window resizing with minimum constraints
        primaryStage.setMinWidth(1000);
        primaryStage.setMinHeight(700);

        // Set initial size
        primaryStage.setWidth(windowWidth);
        primaryStage.setHeight(windowHeight);

        // Center on screen
        primaryStage.centerOnScreen();

        // Add CSS
        scene.getStylesheets().add(getClass().getResource("/modern-style.css").toExternalForm());

        // Keyboard shortcuts
        scene.setOnKeyPressed(event -> {
            if (event.isControlDown() && event.getCode() == KeyCode.S) {
                if (controller != null) controller.saveCurrentGame();
                event.consume();
            } else if (event.isControlDown() && event.getCode() == KeyCode.R) {
                if (controller != null) controller.resetSimulation();
                event.consume();
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}