package com.turtlesamigo.lungabnormdetector;

import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.model.parsers.AbnormalityRecordFileParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SampleOverviewController {
    private final HashMap<String, File> _imageId2File = new HashMap<>();
    @FXML
    private TextField _tfTrainRecordsDir;
    @FXML
    private ImageView _ivSelectedImage;
    @FXML
    private TableView<AbnormalityRecord> _tvRecords;

    @FXML
    public void onBrowseTrainRecordsDir(ActionEvent actionEvent){
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            System.out.println("Directory was not selected.");
            return;
        }

        System.out.println("Selected folder: " + selectedDirectory.getAbsolutePath());

        var recordFiles = selectedDirectory.listFiles((dir, name) -> name.endsWith(".csv"));

        if (recordFiles == null) {
            System.out.println("No record files found.");
            return;
        }

        // Read all abnormality records from csv files.
        var recordsAll = readRecordsFromFiles(recordFiles);

        // Build a map from image id to image file.
        var imageFiles = selectedDirectory.listFiles(SampleOverviewController::isImageFile);
        if (imageFiles != null) {
            for (var imageFile : imageFiles) {
                var imageId = imageFile.getName().split("\\.")[0];
                _imageId2File.put(imageId, imageFile);
            }
        }
    }

    private void fillRecordsTable(List<AbnormalityRecord> records) {
        _tvRecords.getItems().clear();
    }

    private static List<AbnormalityRecord> readRecordsFromFiles(File[] recordFiles) {
        var recordsAll = new ArrayList<AbnormalityRecord>();
        boolean hasError = false;

        for (var recordFile : recordFiles) {
            try {
                var parser = new AbnormalityRecordFileParser(recordFile);
                var records = parser.getRecords();
                recordsAll.addAll(records);
            } catch (Exception e) {
                System.out.println("Failed to parse record file: " + recordFile.getAbsolutePath());
                hasError = true;
            }
        }

        if (hasError) {
            // FX: show alert
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText("Failed to parse some record files.");
            alert.setContentText("Some record files are not parsed correctly. Please check the console for details.");
            alert.showAndWait();
        }

        return recordsAll;
    }

    private static boolean isImageFile(File file) {
        var imageExtensions = Arrays.asList(".png", ".jpg", ".gif", ".bmp", ".jpeg");
        var fileName = file.getName();
        for (var extension : imageExtensions) {
            if (fileName.endsWith(extension)) {
                return true;
            }
        }
        return false;
    }
}
