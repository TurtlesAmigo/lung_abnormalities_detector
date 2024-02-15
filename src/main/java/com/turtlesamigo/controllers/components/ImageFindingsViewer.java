package com.turtlesamigo.controllers.components;

import com.turtlesamigo.model.AbnormalityRecord;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.control.cell.CheckBoxTreeTableCell;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;
import org.opencv.core.Rect2d;

import java.io.File;
import java.util.List;

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

    private File _imageFile;
    private List<AbnormalityRecord> _records;

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
        _imageFile = image;
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

        fillTreeTableView();
        drawBoundingBoxes();
    }

    /**
     * Initializes the tree table view. The tree table view is used to display
     * the records. The records are grouped by radiologist id and finding class.
     */
    private void initTreeTableView() {
        _ttcName.setCellValueFactory(cellData -> cellData.getValue().getValue().nameProperty());
        _ttcIsShown.setCellValueFactory(cellData -> cellData.getValue().getValue().isShownProperty());
        _ttcIsShown.setCellFactory(tc -> new CheckBoxTreeTableCell<>());
    }

    /**
     * Fills the tree table view with the records. The records are grouped by
     * radiologist id and finding class.
     */
    private void fillTreeTableView() {
        _ttvRecordFilter.setRoot(null);
        _ttvRecordFilter.setRoot(new javafx.scene.control.TreeItem<>(new TreeTableRecordItem("Radiologists", true)));
        _ttvRecordFilter.setShowRoot(false);

        var radIds = _records.stream().map(AbnormalityRecord::getRadId).distinct().sorted().toList();

        for (var radId : radIds) {
            var radItem = new TreeTableRecordItem(radId, true);
            var radNode = new javafx.scene.control.TreeItem<>(radItem);
            _ttvRecordFilter.getRoot().getChildren().add(radNode);

            var radIdRecords = _records.stream().filter(r -> r.getRadId().equals(radId)).toList();

            var findingClasses = radIdRecords.stream()
                    .map(AbnormalityRecord::getAbnormalityClass)
                    .distinct().sorted().toList();

            for (var findingClass : findingClasses) {
                var findingItem = new TreeTableRecordItem(findingClass.getClassName(), true);
                var findingNode = new javafx.scene.control.TreeItem<>(findingItem);
                radNode.getChildren().add(findingNode);

                var findingClassRecords = radIdRecords.stream()
                        .filter(r -> r.getAbnormalityClass().equals(findingClass))
                        .toList();

                for (var record : findingClassRecords) {
                    var recordItem = new TreeTableRecordItem(record.getBoundingBox().toString(), true);
                    var recordNode = new javafx.scene.control.TreeItem<>(recordItem);
                    findingNode.getChildren().add(recordNode);
                }
            }
        }
    }

    private void drawBoundingBoxes() {
        _stackPane.getChildren().clear();
        for (var node : _ttvRecordFilter.getRoot().getChildren()) {
            for (var recordNode : node.getChildren()) {
                var recordItem = recordNode.getValue();

                if (!recordItem.isShown()) {
                    continue;
                }

                var rectView = getAdjustedBoundingBoxRect(recordItem);
                _stackPane.getChildren().add(rectView);
            }
        }
    }

    @NotNull
    private Rectangle getAdjustedBoundingBoxRect(TreeTableRecordItem recordItem) {
        var record = recordItem.getRecord();

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
    private static Rect2d adjustRect(double fullHeight, double fullWidth, double newHeight, double newWidth, Rect2d rect) {
        double x = rect.x * newWidth / fullWidth;
        double y = rect.y * newHeight / fullHeight;
        double width = rect.width * newWidth / fullWidth;
        double height = rect.height * newHeight / fullHeight;
        return new Rect2d(x, y, width, height);
    }

    /**
     * The class represents a record item in the tree table view. The item
     * contains the record name and the visibility flag. The visibility flag
     * is used to show or hide the record bounding box on the image.
     */
    private static class TreeTableRecordItem {
        private static final String NAME_PROPERTY_NAME = "Name";
        private static final String IS_SHOWN_PROPERTY_NAME = "IsShown";

        private final StringProperty _nameProperty;
        private final BooleanProperty _isShownProperty;
        private final AbnormalityRecord _record;

        public TreeTableRecordItem(String name, boolean isShown) {
            this(name, isShown, null);
        }

        public TreeTableRecordItem(AbnormalityRecord record) {
            this(record.getBoundingBox().toString(), true, record);
        }

        private TreeTableRecordItem(String name, boolean isShown, AbnormalityRecord record) {
            _nameProperty = new SimpleStringProperty(this, NAME_PROPERTY_NAME, name);
            _isShownProperty = new SimpleBooleanProperty(this, IS_SHOWN_PROPERTY_NAME, isShown);
            _record = record;
        }

        public AbnormalityRecord getRecord() {
            return _record;
        }

        public String getName() {
            return _nameProperty.get();
        }

        public boolean isShown() {
            return _isShownProperty.get();
        }

        public StringProperty nameProperty() {
            return _nameProperty;
        }

        public BooleanProperty isShownProperty() {
            return _isShownProperty;
        }

        public void setIsShown(boolean isShown) {
            _isShownProperty.set(isShown);
        }

        public void setName(String name) {
            _nameProperty.set(name);
        }
    }
}
