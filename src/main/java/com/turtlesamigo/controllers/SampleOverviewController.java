package com.turtlesamigo.controllers;

import com.turtlesamigo.controllers.components.ImageFindingsViewer;
import com.turtlesamigo.controllers.components.RecordSelector;
import com.turtlesamigo.controllers.helpers.AbnormalityRecordView;
import com.turtlesamigo.model.AbnormalityRecord;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import org.opencv.core.Rect2d;

import java.net.URL;
import java.util.*;

public class SampleOverviewController implements Initializable {
    private final HashMap<String, TreeItem<String>> _radId2TreeItem = new HashMap<>();
    public TreeView<String> _recordsTree;
    @FXML
    public Pane _imagePane;
    @FXML
    public BorderPane _borderPane;
    public ImageFindingsViewer _imageFindingsViewer;
    @FXML
    private Pane _overlayBBPane;
    @FXML private RecordSelector _recordSelector;

    private void clearRadItemFolders() {
        List<TreeItem<String>> radItemFolders = _recordsTree.getRoot().getChildren();
        for (TreeItem<String> radiologistItem : radItemFolders) {
            radiologistItem.getChildren().clear();
        }

        var trainData = _recordSelector.getTrainData();

        if (trainData == null || !trainData.isValid()) {
            return;
        }

        for (var record : trainData.getRecordsAll()) {
            var radId = record.getRadId();
            var imageId = record.getImageId();
            assert _radId2TreeItem.containsKey(radId);
            _radId2TreeItem.get(radId).getChildren().add(new TreeItem<>(imageId));
        }
    }

    /**
     * Show the selected image and its records in the table view.
     * @param actionEvent
     */
    @FXML
    public void selectItem(MouseEvent actionEvent) {
        var selectedItem = _recordsTree.getSelectionModel().getSelectedItem();
        var trainData = _recordSelector.getTrainData();

        if (selectedItem == null || trainData == null || !trainData.isValid()) {
            return;
        }

        var imageId = selectedItem.getValue();
        var imageId2File = trainData.getImageId2File();

        if (!imageId2File.containsKey(imageId)) {
            return;
        }

        var imageFile = imageId2File.get(imageId);
        var records = trainData.getImageId2Records().get(imageId);
        _imageFindingsViewer.fillComponent(imageFile, records);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize the records tree.
        var radiologists = new TreeItem<>("Radiologists");
        _recordsTree.setRoot(radiologists);
        var unknownRadItem = new TreeItem<>(AbnormalityRecord.UNKNOWN_RAD_ID_STRING);
        radiologists.getChildren().add(unknownRadItem);
        _radId2TreeItem.put(AbnormalityRecord.UNKNOWN_RAD_ID_STRING, unknownRadItem);

        for (int i = 1; i <= 17; i++) {
            String radId = "R" + i;
            var radiologist = new TreeItem<>(radId);
            radiologists.getChildren().add(radiologist);
            _radId2TreeItem.put(radId, radiologist);
        }

        _recordSelector.trainRecordsDirProperty().addListener((observable, oldValue, newValue) -> {
            clearRadItemFolders();
        });
    }
}
