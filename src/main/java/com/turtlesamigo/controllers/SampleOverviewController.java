package com.turtlesamigo.controllers;

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
    public TableColumn<AbnormalityRecordView, Boolean> _clmnShow;
    public TableColumn<AbnormalityRecordView, String> _clmnRadId;
    public TableColumn<AbnormalityRecordView, String> _clmnFoundingType;
    public TableColumn<AbnormalityRecordView, String> _clmnBBoxSize;
    @FXML
    public Pane _imagePane;
    @FXML
    public BorderPane _borderPane;
    @FXML
    private TableView<AbnormalityRecordView> _tvSelectedRecordData;
    @FXML
    private ImageView _ivSelectedImage;
    @FXML
    private Pane _overlayBBPane;
    @FXML private RecordSelector _recordSelector;

    private void clearRadItemFolders() {
        List<TreeItem<String>> radItemFolders = _recordsTree.getRoot().getChildren();
        for (TreeItem<String> radiologistItem : radItemFolders) {
            radiologistItem.getChildren().clear();
        }
    }

    private void fillRadId2TreeItem() {
        for (var record : _recordSelector.getRecordsAll()) {
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

        if (selectedItem == null) {
            return;
        }

        var imageId = selectedItem.getValue();
        var imageId2File = _recordSelector.getImageId2File();

        if (!_recordSelector.getImageId2File().containsKey(imageId)) {
            return;
        }

        var imageFile = imageId2File.get(imageId);
        var image = new javafx.scene.image.Image(imageFile.toURI().toString());
        _ivSelectedImage.setImage(image);
        showRecords(image, imageId);
    }

    private void showRecords(javafx.scene.image.Image image, String imageId) {
        var records = _recordSelector.getImageId2Records().get(imageId);
        _tvSelectedRecordData.getItems().clear();
        var recordsView = records.stream().map(AbnormalityRecordView::new).sorted(
                Comparator.comparing(AbnormalityRecordView::getRadId)
                        .thenComparing(AbnormalityRecordView::getFoundingType)
                        .thenComparing(AbnormalityRecordView::getBoundingBoxSize)
        ).toList();
        _tvSelectedRecordData.getItems().addAll(recordsView);
        markFindingsOnImage(records, image);
    }

    private void markFindingsOnImage(List<AbnormalityRecord> records, javafx.scene.image.Image image) {
        _overlayBBPane.getChildren().clear();

        var recordsWithFindings = records.stream().filter(AbnormalityRecord::isFinding).toList();

        for (var record : recordsWithFindings) {
            var rect = record.getBoundingBox();
            var adjustedRect = adjustRect(image.getHeight(), image.getWidth(), _ivSelectedImage.getFitHeight(), _ivSelectedImage.getFitWidth(), rect);
            var rectView = new javafx.scene.shape.Rectangle(adjustedRect.x, adjustedRect.y, adjustedRect.width, adjustedRect.height);
            rectView.setStroke(record.getAbnormalityClass().getColor());
            rectView.setFill(null);
            _overlayBBPane.getChildren().add(rectView);
        }
    }

    private static Rect2d adjustRect(double fullHeight, double fullWidth, double newHeight, double newWidth, Rect2d rect) {
        double x = rect.x * newWidth / fullWidth;
        double y = rect.y * newHeight / fullHeight;
        double width = rect.width * newWidth / fullWidth;
        double height = rect.height * newHeight / fullHeight;
        return new Rect2d(x, y, width, height);
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

        // Initialize the table view.
        _clmnShow.setCellValueFactory(cellData -> cellData.getValue().isShownProperty());
        _clmnShow.setCellFactory(cellData -> new CheckBoxTableCell<>());
        _clmnRadId.setCellValueFactory(cellData -> cellData.getValue().radIdProperty());
        _clmnFoundingType.setCellValueFactory(cellData -> cellData.getValue().foundingTypeProperty());
        _clmnBBoxSize.setCellValueFactory(cellData -> cellData.getValue().boundingBoxSizeProperty());

        // Make columns width to fit the content.
        _tvSelectedRecordData.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Always resize image and overlay pane to fit the parent pane.
        _imagePane.widthProperty().addListener((observable, oldValue, newValue) -> {
            _ivSelectedImage.setFitWidth(newValue.doubleValue());
            _overlayBBPane.setPrefWidth(newValue.doubleValue());
        });
        _imagePane.heightProperty().addListener((observable, oldValue, newValue) -> {
            _ivSelectedImage.setFitHeight(newValue.doubleValue());
            _overlayBBPane.setPrefHeight(newValue.doubleValue());
        });
            //FXMLLoader loader = new FXMLLoader();
            //loader.load(Objects.requireNonNull(getClass().getResource("record-selector.fxml")).openStream());
            //_recordSelector = loader.getController();
            //_recordSelector.addOnBrowseAction(this::clearRadItemFolders);
            //_recordSelector.addOnBrowseAction(this::fillRadId2TreeItem);
    }
}
