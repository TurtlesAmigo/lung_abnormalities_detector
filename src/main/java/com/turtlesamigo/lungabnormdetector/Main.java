package com.turtlesamigo.lungabnormdetector;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import nu.pattern.OpenCV;

import java.io.IOException;

public class Main extends Application {
    static {
        OpenCV.loadLocally();
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Lung abnormalities detector");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("main-view.fxml"));
        Parent root = loader.load();

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}