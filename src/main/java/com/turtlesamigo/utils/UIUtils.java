package com.turtlesamigo.utils;

import javafx.scene.control.Alert;

public class UIUtils {
    public static void showAlert(String title, String headerText, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
