package com.turtlesamigo.controllers.helpers;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * A view model for the named boolean property.
 * It is used to display a named boolean property
 * in the TableView.
 */
public class NamedFlag {
    private static final String namePropertyName = "name";
    private static final String isCheckedPropertyName = "Show";

    private final StringProperty _nameProperty;
    private final BooleanProperty _isCheckedProperty;

    public NamedFlag(String name, boolean isChecked) {
        _nameProperty = new SimpleStringProperty(this, namePropertyName, name);
        _isCheckedProperty = new SimpleBooleanProperty(this, isCheckedPropertyName, isChecked);
    }

    public void setName(String name) {
        _nameProperty.set(name);
    }

    public String getName() {
        return _nameProperty.get();
    }

    public boolean isChecked() {
        return _isCheckedProperty.get();
    }

    public void setIsChecked(boolean isChecked) {
        _isCheckedProperty.set(isChecked);
    }

    public StringProperty nameProperty() {
        return _nameProperty;
    }

    public BooleanProperty isCheckedProperty() {
        return _isCheckedProperty;
    }
}
