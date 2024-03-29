package com.turtlesamigo.controllers.components;

import com.turtlesamigo.controllers.helpers.TreeTableRecordItem;
import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Rect2d;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The component displays the image and the findings.
 */
public class ImageFindingsViewer extends VBox {
    @FXML private TextField _tfFilePath;
    @FXML private Pane _stackPane;
    @FXML private Label _lblNoImage;
    @FXML private ImageView _imageView;
    @FXML private TreeTableView<TreeTableRecordItem> _ttvRecordFilter;
    @FXML private TreeTableColumn<TreeTableRecordItem, String> _ttcName;
    @FXML private TreeTableColumn<TreeTableRecordItem, Boolean> _ttcIsShown;

    private List<AbnormalityRecord> _records;
    private final Map<AbnormalityRecord, Rectangle> _record2Rect = new HashMap<>();
    private final Map<AbnormalityRecord, Label> _record2Label = new HashMap<>();

    public ImageFindingsViewer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("image-findings-viewer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        initTreeTableView();
    }

    public void fillComponentWithImageRecords(File imageFile, List<AbnormalityRecord> records) {
        _records = records;
        _lblNoImage.setVisible(imageFile == null);

        if (imageFile == null) {
            _tfFilePath.clear();
            _imageView.setImage(null);
            displayFindings();
            return;
        }

        _tfFilePath.setText(imageFile.getAbsolutePath());
        var image = new javafx.scene.image.Image(imageFile.toURI().toString());
        _imageView.setImage(image);

        _record2Rect.clear();
        _record2Label.clear();
        var findings = records.stream().filter(AbnormalityRecord::isFinding).toList();
        for (var record : findings) {
            var rectView = getAdjustedBoundingBoxRect(record);
            _record2Rect.put(record, rectView);
            var label = new Label(record.getRadId() + " - " + record.getAbnormalityClass().getClassName());
            _record2Label.put(record, label);
        }

        displayFindings();
    }

    /**
     * Initializes the tree table view. The tree table view is used to display
     * the records. The records are grouped by radiologist id and finding class.
     */
    private void initTreeTableView() {
        _ttcName.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
        _ttvRecordFilter.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        _ttcIsShown.setCellValueFactory(cellData -> cellData.getValue().getValue().isShownProperty());
        _ttcIsShown.setCellFactory(tc -> new CheckBoxTreeTableCell<>());
        
        _imageView.fitWidthProperty().bind(_stackPane.widthProperty());
        _imageView.fitHeightProperty().bind(_stackPane.heightProperty());

        _imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> redrawAllRects());
        _imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> redrawAllRects());
    }

    private void displayFindings() {
        refillRecordFilter();
        redrawAllRects();
    }

    /**
     * Fills the tree table view with the records related to the image. The records are grouped by
     * radiologist id and finding class. The visibility flag is used to show or hide the
     * record bounding box on the image. If the radiologist node is hidden, all
     * records of the radiologist are hidden as well. If the radiologist node is
     * shown, all records of the radiologist are shown as well.
     */
    private void refillRecordFilter() {
        var root = new javafx.scene.control.TreeItem<>(new TreeTableRecordItem("Radiologists", true));
        _ttvRecordFilter.setRoot(root);
        _ttvRecordFilter.setShowRoot(false);

        if (_records == null) {
            return;
        }

        var radIds = _records.stream().map(AbnormalityRecord::getRadId).distinct().sorted().toList();

        for (var radId : radIds) {
            var radItem = new TreeTableRecordItem(radId, true);
            var radNode = new javafx.scene.control.TreeItem<>(radItem);
            root.getChildren().add(radNode);

            // Add the listener to the radiologist node.
            UIUtils.addTreeTableViewFlagListener(radItem, radNode);

            var radIdRecords = _records.stream().filter(r -> r.getRadId().equals(radId)).toList();

            var findingClasses = radIdRecords.stream()
                    .map(AbnormalityRecord::getAbnormalityClass)
                    .distinct().sorted().toList();

            for (var findingClass : findingClasses) {
                var findingItem = new TreeTableRecordItem(findingClass.getClassName(), true);
                var findingNode = new javafx.scene.control.TreeItem<>(findingItem);
                radNode.getChildren().add(findingNode);

                // Add the listener to the finding node.
                UIUtils.addTreeTableViewFlagListener(findingItem, findingNode);

                var findingClassRecords = radIdRecords.stream()
                        .filter(r -> r.getAbnormalityClass().equals(findingClass))
                        .toList();

                for (var record : findingClassRecords) {
                    var recordItem = new TreeTableRecordItem(record);
                    var recordNode = new javafx.scene.control.TreeItem<>(recordItem);
                    findingNode.getChildren().add(recordNode);

                    if (!record.isFinding()) {
                        continue;
                    }

                    recordItem.isShownProperty().addListener((observable, oldValue, newValue) -> {
                        updateFoundingRect(record, false, newValue);
                    });
                }
            }
        }
    }

    private void updateFoundingRect(AbnormalityRecord record, boolean recalculateBounds, boolean isShown) {
        if (record == null || !record.isFinding()) {
            return;
        }

        var rectView = _record2Rect.get(record);
        var label = _record2Label.get(record);
        _stackPane.getChildren().remove(rectView);
        _stackPane.getChildren().remove(label);

        if (recalculateBounds) {
            rectView = getAdjustedBoundingBoxRect(record);
            _record2Rect.replace(record, rectView);
        }

        if (isShown) {
            _stackPane.getChildren().add(rectView);
            // Add label with the radiologist id and finding name close to rect.
            label.setLayoutX(rectView.getX());
            label.setLayoutY(rectView.getY());
            _stackPane.getChildren().add(label);
        }
    }

    private void redrawAllRects() {
        _stackPane.getChildren().removeIf(r -> r instanceof Rectangle);
        _stackPane.getChildren().removeIf(r -> r instanceof Label);
        _stackPane.getChildren().add(_lblNoImage);
        var root = _ttvRecordFilter.getRoot();

        if (root == null) {
            return;
        }

        checkTreeItemChildrenFlags(root);
    }

    /**
     * Checks the children of the tree item and updates the visibility of the
     * bounding boxes on the image. When the parent flag is changed, the children
     * flags should be updated accordingly.
     * @param root The root tree item.
     */
    private void checkTreeItemChildrenFlags(TreeItem<TreeTableRecordItem> root) {
        var recordChildren = root.getChildren().stream().filter(r -> r.getValue().getRecord() != null).toList();

        if (recordChildren.isEmpty()) {
            for (var node : root.getChildren()) {
                checkTreeItemChildrenFlags(node);
            }
            return;
        }

        for (var recordNode : recordChildren) {
            var recordItem = recordNode.getValue();
            updateFoundingRect(recordItem.getRecord(), true, recordItem.isShown());
        }
    }

    /**
     * Returns the bounding box rectangle of the record adjusted
     * to the new image size.
     * @param record The record to adjust the bounding box for.
     * @return The adjusted bounding box rectangle.
     */
    @NotNull
    private Rectangle getAdjustedBoundingBoxRect(AbnormalityRecord record) {
        var rect = record.getBoundingBox();
        var image = _imageView.getImage();
        var adjustedRect = adjustRect(image.getHeight(), image.getWidth(),
                _imageView.getFitHeight(), _imageView.getFitWidth(), rect);
        adjustedRect.setStroke(record.getAbnormalityClass().getColor());
        adjustedRect.setFill(null);
        return adjustedRect;
    }

    /**
     * Adjusts the bounding box coordinates to the new image size.
     * @param originalHeight The full height of the original image.
     * @param originalWidth The full width of the original image.
     * @param newHeight The new height of the image.
     * @param newWidth The new width of the image.
     * @param rect The bounding box to adjust.
     * @return The adjusted bounding box.
     */
    private static Rectangle adjustRect(double originalHeight,
                                     double originalWidth,
                                     double newHeight,
                                     double newWidth,
                                     Rect2d rect) {
        double x = rect.x * newWidth / originalWidth;
        double y = rect.y * newHeight / originalHeight;
        double width = rect.width * newWidth / originalWidth;
        double height = rect.height * newHeight / originalHeight;
        return new Rectangle(x, y, width, height);
    }
}
