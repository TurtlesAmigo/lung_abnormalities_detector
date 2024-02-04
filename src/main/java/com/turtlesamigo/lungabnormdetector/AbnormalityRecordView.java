package com.turtlesamigo.lungabnormdetector;

import com.turtlesamigo.model.AbnormalityRecord;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.opencv.core.Rect2d;

/**
 * A view model for the abnormality record.
 * It is used to display the record in the TableView.
 */
class AbnormalityRecordView {
    private final BooleanProperty _isShown;
    private final StringProperty _radId;
    private final StringProperty _foundingType;
    private final StringProperty _boundingBoxSize;

    /**
     * Creates a new instance of the view model.
     * @param record The record to display.
     */
    public AbnormalityRecordView(AbnormalityRecord record) {
        _isShown = new SimpleBooleanProperty(this, "isShown", true);
        _radId = new SimpleStringProperty(this, "radId", record.getRadId());
        _foundingType = new SimpleStringProperty(this,
                "foundingType",
                record.getAbnormalityClass().getClassName());
        _boundingBoxSize = new SimpleStringProperty(this,
                "boundingBoxSize",
                getBoundingBoxSizeString(record.getBoundingBox()));
    }

    /**
     * Sets the visibility of the record bounding box
     * on the image.
     * @param isShown The visibility of the record.
     */
    public void setIsShown(boolean isShown) {
        _isShown.set(isShown);
    }

    /**
     * Returns the visibility of the record bounding box
     * on the image.
     * @return True, if the record should be shown on the image;
     * otherwise, false.
     */
    public Boolean isShown() {
        return _isShown.get();
    }

    public String getRadId() {
        return _radId.get();
    }

    public String getFoundingType() {
        return _foundingType.get();
    }

    public String getBoundingBoxSize() {
        return _boundingBoxSize.get();
    }

    public BooleanProperty isShownProperty() {
        return _isShown;
    }

    public StringProperty radIdProperty() {
        return _radId;
    }

    public StringProperty foundingTypeProperty() {
        return _foundingType;
    }

    public StringProperty boundingBoxSizeProperty() {
        return _boundingBoxSize;
    }

    /**
     * Returns a string representation of the bounding box size.
     * @param boundingBox The bounding box to represent.
     * @return The string representation of the bounding box size.
     */
    private static String getBoundingBoxSizeString(Rect2d boundingBox) {
        return boundingBox == null ? "-" : (int)boundingBox.width + "x" + (int)boundingBox.height;
    }
}
