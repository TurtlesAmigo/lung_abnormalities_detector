package com.turtlesamigo.controllers;

import com.turtlesamigo.controllers.components.ImageFindingsViewer;
import com.turtlesamigo.controllers.components.RecordsLoader;
import com.turtlesamigo.model.AbnormalityRecord;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

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
    public PieChart _pieChart;
    @FXML
    private Pane _overlayBBPane;
    @FXML private RecordsLoader _recordsLoader;

    private void refillRadItemFolders() {
        List<TreeItem<String>> radItemFolders = _recordsTree.getRoot().getChildren();
        for (TreeItem<String> radiologistItem : radItemFolders) {
            radiologistItem.getChildren().clear();
        }

        var trainData = _recordsLoader.getTrainData();

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
        var trainData = _recordsLoader.getTrainData();

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

        _recordsLoader.trainRecordsDirProperty().addListener((observable, oldValue, newValue) -> {
            refillRadItemFolders();
            fillPieChart();
        });
    }

    private void fillPieChart() {
        var trainData = _recordsLoader.getTrainData();

        if (trainData == null || !trainData.isValid()) {
            return;
        }

        var records = trainData.getRecordsAll();
        var findingClasses = records.stream()
                .map(AbnormalityRecord::getAbnormalityClass)
                .distinct()
                .sorted()
                .toList();

        var findingClass2Count = new HashMap<String, Integer>();

        for (var findingClass : findingClasses) {
            findingClass2Count.put(findingClass.getClassName(), 0);
        }

        for (var record : records) {
            var findingClass = record.getAbnormalityClass().getClassName();
            findingClass2Count.put(findingClass, findingClass2Count.get(findingClass) + 1);
        }

        var pieChartData = new ArrayList<PieChart.Data>();

        for (var entry : findingClass2Count.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " - " + entry.getValue(), entry.getValue()));
        }

        _pieChart.setData(FXCollections.observableArrayList(pieChartData));
    }
}
