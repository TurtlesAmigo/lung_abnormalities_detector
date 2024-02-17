package com.turtlesamigo.utils;

import com.turtlesamigo.controllers.helpers.TreeTableRecordItem;
import javafx.scene.control.Alert;
import javafx.scene.control.TreeItem;

public class UIUtils {
    public static void showAlert(String title, String headerText, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Adds a listener to the TreeTableView item to update the visibility of the child items.
     * If the item is hidden, all child items will be hidden as well. If the item is shown, all
     * child items will be shown as well.
     * @param item The item to add the listener to.
     * @param treeNode The tree node to update the visibility of the child items.
     */
    public static void addTreeTableViewFlagListener(TreeTableRecordItem item, TreeItem<TreeTableRecordItem> treeNode) {
        item.isShownProperty().addListener((observable, oldValue, newValue) -> {
            for (var recordNode : treeNode.getChildren()) {
                recordNode.getValue().setIsShown(newValue);
            }
        });
    }
}
