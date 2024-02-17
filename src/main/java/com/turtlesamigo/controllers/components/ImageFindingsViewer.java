package com.turtlesamigo.controllers.components;

import com.turtlesamigo.controllers.helpers.TreeTableRecordItem;
import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.utils.UIUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
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
    @FXML private StackPane _stackPane;
    @FXML private Label _lblNoImage;
    @FXML private ImageView _imageView;
    @FXML private TreeTableView<TreeTableRecordItem> _ttvRecordFilter;
    @FXML private TreeTableColumn<TreeTableRecordItem, String> _ttcName;
    @FXML private TreeTableColumn<TreeTableRecordItem, Boolean> _ttcIsShown;

    private List<AbnormalityRecord> _records;
    private final Map<AbnormalityRecord, Rectangle> _record2Rect = new HashMap<>();

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

    public void fillComponent(File image, List<AbnormalityRecord> records) {
        _records = records;

        if (image == null) {
            _lblNoImage.setVisible(true);
            _stackPane.setVisible(false);
            return;
        }

        _lblNoImage.setVisible(false);
        _stackPane.setVisible(true);
        _tfFilePath.setText(image.getAbsolutePath());
        _imageView.setImage(new javafx.scene.image.Image(image.toURI().toString()));

        // TODO: Check fill map / updateComponent order
        _record2Rect.clear();
        for (var record : records) {
            var rectView = getAdjustedBoundingBoxRect(record);
            _record2Rect.put(record, rectView);
        }

        updateComponent();
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

        // TODO: Check, if works.
        // Resize rects whenever the image is resized.
        _imageView.fitWidthProperty().bind(_stackPane.widthProperty());
        _imageView.fitHeightProperty().bind(_stackPane.heightProperty());
        _imageView.fitWidthProperty().addListener((observable, oldValue, newValue) -> redrawAllRects());
        _imageView.fitHeightProperty().addListener((observable, oldValue, newValue) -> redrawAllRects());

    }

    private void updateComponent() {
        refillTreeTableView();
        redrawAllRects();
    }

    /**
     * Fills the tree table view with the records. The records are grouped by
     * radiologist id and finding class. The visibility flag is used to show or hide the
     * record bounding box on the image. If the radiologist node is hidden, all
     * records of the radiologist are hidden as well. If the radiologist node is
     * shown, all records of the radiologist are shown as well.
     */
    private void refillTreeTableView() {
        var root = new javafx.scene.control.TreeItem<>(new TreeTableRecordItem("Radiologists", true));
        _ttvRecordFilter.setRoot(root);
        _ttvRecordFilter.setShowRoot(false);

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
                    var recordItem = new TreeTableRecordItem(record.getBoundingBox().toString(), true);
                    var recordNode = new javafx.scene.control.TreeItem<>(recordItem);
                    findingNode.getChildren().add(recordNode);
                    recordItem.isShownProperty().addListener((observable, oldValue, newValue) -> {
                        updateFoundingRect(record, false, newValue);
                    });
                }
            }
        }
    }

    private void updateFoundingRect(AbnormalityRecord record, boolean recalculateBounds, boolean isShown) {
        var rectView = _record2Rect.get(record);
        _stackPane.getChildren().remove(rectView);

        if (recalculateBounds) {
            rectView = getAdjustedBoundingBoxRect(record);
            _record2Rect.replace(record, rectView);
        }

        if (isShown) {
            _stackPane.getChildren().add(rectView);
        }
    }

    private void redrawAllRects() {
        _stackPane.getChildren().removeIf(r -> r instanceof Rectangle);

        if (_ttvRecordFilter.getRoot() == null) {
            return;
        }

        for (var node : _ttvRecordFilter.getRoot().getChildren()) {
            for (var recordNode : node.getChildren()) {
                var recordItem = recordNode.getValue();
                updateFoundingRect(recordItem.getRecord(), true, recordItem.isShown());
            }
        }
    }

    @NotNull
    private Rectangle getAdjustedBoundingBoxRect(AbnormalityRecord record) {
        var rect = record.getBoundingBox();
        var image = _imageView.getImage();
        var adjustedRect = adjustRect(image.getHeight(), image.getWidth(),
                _imageView.getFitHeight(), _imageView.getFitWidth(), rect);
        var rectView = new Rectangle(adjustedRect.x, adjustedRect.y,
                adjustedRect.width, adjustedRect.height);
        rectView.setStroke(record.getAbnormalityClass().getColor());
        rectView.setFill(null);
        return rectView;
    }

    /**
     * Adjusts the bounding box coordinates to the new image size.
     * @param fullHeight The full height of the original image.
     * @param fullWidth The full width of the original image.
     * @param newHeight The new height of the image.
     * @param newWidth The new width of the image.
     * @param rect The bounding box to adjust.
     * @return The adjusted bounding box.
     */
    private static Rect2d adjustRect(double fullHeight,
                                     double fullWidth,
                                     double newHeight,
                                     double newWidth,
                                     Rect2d rect) {
        double x = rect.x * newWidth / fullWidth;
        double y = rect.y * newHeight / fullHeight;
        double width = rect.width * newWidth / fullWidth;
        double height = rect.height * newHeight / fullHeight;
        return new Rect2d(x, y, width, height);
    }
}
