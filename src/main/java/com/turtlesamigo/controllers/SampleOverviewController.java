package com.turtlesamigo.controllers;

import com.turtlesamigo.collections.MapOfLists;
import com.turtlesamigo.controllers.components.ImageFindingsViewer;
import com.turtlesamigo.controllers.components.RecordsLoader;
import com.turtlesamigo.controllers.helpers.NamedFlag;
import com.turtlesamigo.model.AbnormalityClass;
import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.model.TrainData;
import com.turtlesamigo.utils.UIUtils;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class SampleOverviewController implements Initializable {
    private final HashMap<String, TreeItem<String>> _radId2TreeItem = new HashMap<>();
    public TreeView<String> _recordsTree;
    @FXML private Pane _imagePane;
    @FXML private BorderPane _borderPane;
    @FXML
    private ImageFindingsViewer _imageFindingsViewer;
    @FXML private PieChart _pieChart;
    @FXML private Pane _overlayBBPane;
    @FXML private RecordsLoader _recordsLoader;

    // The table view for the radiologists filtering.
    @FXML private TableView<NamedFlag> _tvAllowedRadiologists;
    @FXML private TableColumn<NamedFlag, Boolean> _tcRadiologistShow;
    @FXML private TableColumn<NamedFlag, String> _tcRadiologistName;

    // The table view for the finding classes filtering.
    @FXML private TableView<NamedFlag> _tvAllowedFindingClasses;
    @FXML private TableColumn<NamedFlag, Boolean> _tcFindingShow;
    @FXML private TableColumn<NamedFlag, String> _tcFindingName;

    /**
     * Refill the records components based on the train data.
     * @param trainData the train data.
     */
    private void refillRecordsComponents(TrainData trainData) {
        List<TreeItem<String>> radItemFolders = _recordsTree.getRoot().getChildren();
        for (TreeItem<String> radiologistItem : radItemFolders) {
            radiologistItem.getChildren().clear();
        }

        if (trainData == null || !trainData.isValid()) {
            return;
        }

        for (var record : trainData.getRecordsAll()) {
            var radId = record.getRadId();
            var imageId = record.getImageId();
            assert _radId2TreeItem.containsKey(radId);
            _radId2TreeItem.get(radId).getChildren().add(new TreeItem<>(imageId));
        }

        _recordsTree.getRoot().setExpanded(true);

        UIUtils.showAlert("Records loaded", "Records have been loaded successfully.",
                "", Alert.AlertType.INFORMATION);
    }

    /**
     * Show the selected image and its records in the table view.
     */
    @FXML
    public void selectItem() {
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

        setupFilteringTableViews();

        _recordsLoader.trainRecordsDirProperty().addListener((observable, oldValue, newValue) -> {
            refillRecordsComponents(_recordsLoader.getTrainData());
            fillFilteringTableViews();
            fillPieChart();
        });
    }

    private void setupFilteringTableViews() {
        // TODO: Make checkboxes editable when needed.

        // Setup _tvAllowedRadiologists.
        _tvAllowedRadiologists.setEditable(false);
        _tvAllowedRadiologists.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        _tcRadiologistShow.setEditable(false);
        _tcRadiologistShow.setCellFactory(cellData -> new CheckBoxTableCell<>());
        _tcRadiologistShow.setCellValueFactory(cellData -> cellData.getValue().isCheckedProperty());
        _tcRadiologistName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Setup _tvAllowedFindingClasses.
        _tvAllowedFindingClasses.setEditable(false);
        _tvAllowedFindingClasses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        _tcFindingShow.setEditable(false);
        _tcFindingShow.setCellFactory(cellData -> new CheckBoxTableCell<>());
        _tcFindingShow.setCellValueFactory(cellData -> cellData.getValue().isCheckedProperty());
        _tcFindingName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }

    private void fillFilteringTableViews() {
        var trainData = _recordsLoader.getTrainData();
        if (trainData == null || !trainData.isValid()) {
            return;
        }

        var records = trainData.getRecordsAll();
        var radIdRows = records.stream().map(AbnormalityRecord::getRadId).distinct().sorted()
                .map(radId -> new NamedFlag(radId, true)).toList();
        var findingClassesRows = records.stream().map(AbnormalityRecord::getAbnormalityClass).distinct().sorted()
                .map(fc -> new NamedFlag(fc.getClassName(), true)).toList();

        // Add filtering listeners to the filtering table view items.
        ChangeListener<Boolean> listener = (observable, oldValue, newValue) -> {
            var filteredTrainData = getFilteredTrainData();
            refillRecordsComponents(filteredTrainData);
            fillPieChart();
        };

        for (var radIdRow : radIdRows) {
            radIdRow.isCheckedProperty().addListener(listener);
        }

        for (var findingClassesRow : findingClassesRows) {
            findingClassesRow.isCheckedProperty().addListener(listener);
        }

        _tvAllowedRadiologists.getItems().clear();
        _tvAllowedRadiologists.getItems().addAll(radIdRows);

        _tvAllowedFindingClasses.getItems().clear();
        _tvAllowedFindingClasses.getItems().addAll(findingClassesRows);
    }

    /**
     * Fill the pie chart with the finding classes statistics.
     */
    private void fillPieChart() {
        var trainData = _recordsLoader.getTrainData();

        if (trainData == null || !trainData.isValid()) {
            return;
        }

        MapOfLists<String, AbnormalityRecord> imageId2Records = new MapOfLists<>();

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
            // var findingClass = record.getAbnormalityClass().getClassName();
            // findingClass2Count.put(findingClass, findingClass2Count.get(findingClass) + 1);

            imageId2Records.add(record.getImageId(), record);
        }

        for (var entry : imageId2Records.entrySet()) {
            var findingClassesInImage = entry.getValue().stream()
                    .map(AbnormalityRecord::getAbnormalityClass)
                    .map(AbnormalityClass::getClassName)
                    .collect(Collectors.toSet());

            for (var findingClass : findingClassesInImage) {
                findingClass2Count.put(findingClass, findingClass2Count.get(findingClass) + 1);
            }
        }

        var pieChartData = new ArrayList<PieChart.Data>();

        for (var entry : findingClass2Count.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey() + " - " + entry.getValue(), entry.getValue()));
        }

        _pieChart.setData(FXCollections.observableArrayList(pieChartData));
    }

    /**
     * Get the filtered train data. The filtering is based on the selected radiologists and finding classes.
     * The selection is done in the table views _tvAllowedRadiologists and _tvAllowedFindingClasses.
     * @return the filtered train data.
     */
    private TrainData getFilteredTrainData() {
        var trainData = _recordsLoader.getTrainData();
        if (trainData == null || !trainData.isValid()) {
            return null;
        }

        var records = trainData.getRecordsAll();
        var radId2Show = _tvAllowedRadiologists.getItems().stream()
                .filter(NamedFlag::isChecked)
                .map(NamedFlag::getName)
                .collect(Collectors.toSet());
        var findingClass2Show = _tvAllowedFindingClasses.getItems().stream()
                .filter(NamedFlag::isChecked)
                .map(NamedFlag::getName)
                .collect(Collectors.toSet());

        var filteredRecords = records.stream()
                .filter(record -> radId2Show.contains(record.getRadId()))
                .filter(record -> findingClass2Show.contains(record.getAbnormalityClass().getClassName()))
                .toList();

        return new TrainData(trainData.getTrainDirectory(), filteredRecords);
    }
}
