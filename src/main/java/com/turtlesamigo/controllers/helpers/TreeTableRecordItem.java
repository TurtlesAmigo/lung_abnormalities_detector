package com.turtlesamigo.controllers.helpers;

import com.turtlesamigo.model.AbnormalityRecord;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * The class represents a record item in the tree table view. The item
 * contains the record name and the visibility flag. The visibility flag
 * is used to show or hide the record bounding box on the image.
 */
public class TreeTableRecordItem {
    private static final String NAME_PROPERTY_NAME = "Name";
    private static final String IS_SHOWN_PROPERTY_NAME = "IsShown";

    private final StringProperty _nameProperty;
    private final BooleanProperty _isShownProperty;
    private final AbnormalityRecord _record;

    public TreeTableRecordItem(String name, boolean isShown) {
        this(name, isShown, null);
    }

    public TreeTableRecordItem(AbnormalityRecord record) {
        this(record.getBoundingBoxString(), record.isFinding(), record);
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
