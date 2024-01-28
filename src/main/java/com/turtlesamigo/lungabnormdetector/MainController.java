package com.turtlesamigo.lungabnormdetector;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;

public class MainController {
    @FXML
    public ImageView imageView;
    @FXML
    private Label welcomeText;
    @FXML
    private Label fileNameLabel;
    @FXML
    private Label fileSizeLabel;

    @FXML
    public void onOpenImage() {
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
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void onSampleOverview(ActionEvent actionEvent) throws Exception {
        Stage stage = new Stage();
        stage.setTitle("Sample overview tool");

        FXMLLoader loader = new FXMLLoader(getClass().getResource("sample-overview.fxml"));
        Parent root = loader.load();

        stage.setScene(new Scene(root, 800, 600));
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();
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

    @FXML
    public void exitApplication(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}