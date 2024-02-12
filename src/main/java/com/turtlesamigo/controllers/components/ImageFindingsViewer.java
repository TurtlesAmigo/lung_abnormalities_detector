package com.turtlesamigo.controllers.components;

import com.turtlesamigo.model.AbnormalityRecord;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

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

    public ImageFindingsViewer() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("image-findings-viewer.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

    public void fillComponent(File image, List<AbnormalityRecord> records) {

    }
}
