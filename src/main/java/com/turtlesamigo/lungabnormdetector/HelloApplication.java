package com.turtlesamigo.lungabnormdetector;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

public class HelloApplication extends Application {
    private ImageView imageView = new ImageView();
    private Label fileNameLabel = new Label("File: ");
    private Label fileSizeLabel = new Label("Size: ");

    @Override
    public void start(Stage primaryStage) throws IOException {
        primaryStage.setTitle("Image Open App");

        MenuBar menuBar = new MenuBar();
        Menu fileMenu = new Menu("File");
        MenuItem openItem = new MenuItem("Open");
        openItem.setOnAction(this::openImage);
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setOnAction(e -> System.exit(0));
        fileMenu.getItems().addAll(openItem, new SeparatorMenuItem(), exitItem);

        menuBar.getMenus().addAll(fileMenu);

        VBox vbox = new VBox(menuBar);

        Group root = new Group(vbox);
        vbox.getChildren().addAll(imageView, fileNameLabel, fileSizeLabel);

        primaryStage.setScene(new Scene(root, 800, 600));
        primaryStage.show();
    }

    private void openImage(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Image File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif", "*.bmp")
        );

        File selectedFile = fileChooser.showOpenDialog(null);
        if (selectedFile != null) {
            try {
                Image image = new Image(selectedFile.toURI().toString());
                imageView.setImage(image);

                // Update labels
                fileNameLabel.setText("File: " + selectedFile.getName());
                fileSizeLabel.setText("Size: " + selectedFile.length() / 1024 + " KB");
            } catch (Exception e) {
                showAlert("Error", "Could not open the image.");
            }
        }
    }

    private void detectAbnormalities(ActionEvent event) {
        showAlert("Detect Abnormalities", "Detecting abnormalities on the image...");
        // Add your logic for detecting abnormalities here
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch();
    }
}