package com.turtlesamigo.lungabnormdetector;

import com.turtlesamigo.collections.MapOfLists;
import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.model.parsers.AbnormalityRecordFileParser;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.ImageView;
import javafx.stage.DirectoryChooser;

import java.io.File;
import java.net.URL;
import java.util.*;

public class SampleOverviewController implements Initializable {
    private final HashMap<String, File> _imageId2File = new HashMap<>();
    private final MapOfLists<String, AbnormalityRecord> _imageId2Records = new MapOfLists<>();
    private final HashMap<String, TreeItem> _radId2TreeItem = new HashMap<>();
    public TreeView<String> _recordsTree;
    @FXML
    private TextField _tfTrainRecordsDir;
    @FXML
    private ImageView _ivSelectedImage;

    /**
     * Browse the directory containing the abnormality records.
     * The directory should contain csv files with the following columns:
     *  - image_id;
     *  - class_name;
     *  - class_id;
     *  - rad_id;
     *  - x_min;
     *  - y_min;
     *  - x_max;
     *  - y_max.
     *  Fills _recordsTree with the records from the csv files. Also fills
     *  _imageId2File and _imageId2Records. ImageId keys correspond to image
     *  file names without extension in the selected folder.
     * @param actionEvent
     */
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
            showAlert("Warning",
                    "No record files found.",
                    "No record files found in the selected directory.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Read all abnormality records from csv files.
        var recordsAll = readRecordsFromFiles(recordFiles);
        if (recordsAll.isEmpty()) {
            System.out.println("No records found.");
            showAlert("Warning",
                    "No records found.",
                    "No records found in the selected directory.",
                    Alert.AlertType.WARNING);
            return;
        }

        List<TreeItem<String>> radiologists = _recordsTree.getRoot().getChildren();
        for (TreeItem<String> radiologistItem : radiologists) {
            radiologistItem.getChildren().clear();
        }

        for (var record : recordsAll) {
            _imageId2Records.add(record.getImageId(), record);
            assert _radId2TreeItem.containsKey(record.getRadId());
            _radId2TreeItem.get(record.getRadId()).getChildren().add(new TreeItem<>(record.getImageId()));
        }

        // Build a map from image id to image file.
        var imageFiles = selectedDirectory.listFiles(SampleOverviewController::isImageFile);
        if (imageFiles != null) {
            _imageId2File.clear();
            for (var imageFile : imageFiles) {
                var imageId = imageFile.getName().split("\\.")[0];
                _imageId2File.put(imageId, imageFile);
            }
        }
    }

    private void fillRecordsTable(List<AbnormalityRecord> records) {
        //_tvRecords.getItems().clear();
    }

    @FXML
    public void selectItem(ActionEvent actionEvent) {

    }

    private void fillTreeView() {
    }

    private static void showAlert(String title, String headerText, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(message);
        alert.showAndWait();
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
            showAlert("Warning",
                    "Failed to parse some record files.",
                    "Some record files are not parsed correctly. Please check the console for details.",
                     Alert.AlertType.WARNING);
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        var radiologists = new TreeItem<>("Radiologists");
        _recordsTree.setRoot(radiologists);

        for (int i = 1; i <= 17; i++) {
            String radId = "R" + i;
            var radiologist = new TreeItem<>(radId);
            radiologists.getChildren().add(radiologist);
            _radId2TreeItem.put(radId, radiologist);
        }
    }
}
