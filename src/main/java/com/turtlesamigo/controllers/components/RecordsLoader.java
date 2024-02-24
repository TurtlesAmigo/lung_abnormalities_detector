package com.turtlesamigo.controllers.components;

import com.turtlesamigo.model.TrainData;
import com.turtlesamigo.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Pair;

import java.io.File;

/**
 * The component allows to load the training data.
 */
public class RecordsLoader extends VBox {
    @FXML private Button _btnBrowse;
    @FXML private TextField _tfTrainRecordsDir;

    @FXML private TableView<Pair<StringProperty, StringProperty>> _tvTrainDataStats;
    @FXML private TableColumn<Pair<StringProperty,StringProperty>, String> _tcProperty;
    @FXML private TableColumn<Pair<StringProperty,StringProperty>, String> _tcValue;

    private TrainData _trainData;

    private final StringProperty _trainRecordsDirProperty = new SimpleStringProperty(this, "trainRecordsDir");

    public RecordsLoader() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("records-loader.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);

        try {
            fxmlLoader.load();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }

        _btnBrowse.setOnAction(this::onBrowseTrainRecordsDir);
        _tvTrainDataStats.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    public TrainData getTrainData() {
        return _trainData;
    }

    public StringProperty trainRecordsDirProperty() {
        return _trainRecordsDirProperty;
    }

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
     */
    private void onBrowseTrainRecordsDir(ActionEvent actionEvent) {
        // Use DirectoryChooser to choose a directory with csv and images.
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory == null) {
            System.out.println("Directory was not selected.");
            return;
        }

        _trainData = new TrainData(selectedDirectory);

        if (!_trainData.isValid()) {
            System.out.println("Invalid train data.");
            UIUtils.showAlert("Invalid train data", "The selected folder does not contain valid train data.",
                    "Please, select another folder.", Alert.AlertType.WARNING);
        }

        String trainRecordsDir = selectedDirectory.getAbsolutePath();
        System.out.println("Selected folder: " + trainRecordsDir);
        _tfTrainRecordsDir.setText(trainRecordsDir);
        _trainRecordsDirProperty.set(trainRecordsDir);
        fillTrainDataStats();
    }

    private void fillTrainDataStats() {
        if (_trainData == null || !_trainData.isValid()) {
            return;
        }

        // TODO: fill the table view with the train data stats.

        _tvTrainDataStats.getItems().clear();
    }
}
