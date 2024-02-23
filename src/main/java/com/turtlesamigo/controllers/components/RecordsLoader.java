package com.turtlesamigo.controllers.components;

import com.turtlesamigo.controllers.helpers.NamedFlag;
import com.turtlesamigo.model.TrainData;
import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.utils.UIUtils;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.DirectoryChooser;

import java.io.File;

/**
 * The component allows to load the training data.
 */
public class RecordsLoader extends VBox {
    @FXML private Button _btnBrowse;
    @FXML private TextField _tfTrainRecordsDir;

    @FXML private TableView<NamedFlag> _tvAllowedRadiologists;
    @FXML private TableColumn<NamedFlag, Boolean> _tcRadiologistShow;
    @FXML private TableColumn<NamedFlag, String> _tcRadiologistName;

    @FXML private TableView<NamedFlag> _tvAllowedFindingClasses;
    @FXML private TableColumn<NamedFlag, Boolean> _tcFindingShow;
    @FXML private TableColumn<NamedFlag, String> _tcFindingName;

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

        setupTableViews();
        _btnBrowse.setOnAction(this::onBrowseTrainRecordsDir);
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
        fillTableViews();
    }

    private void setupTableViews() {
        // Setup _tvAllowedRadiologists.
        _tvAllowedRadiologists.setEditable(true);
        _tvAllowedRadiologists.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        _tcRadiologistShow.setEditable(true);
        _tcRadiologistShow.setCellFactory(cellData -> new CheckBoxTableCell<>());
        _tcRadiologistShow.setCellValueFactory(cellData -> cellData.getValue().isCheckedProperty());
        _tcRadiologistName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());

        // Setup _tvAllowedFindingClasses.
        _tvAllowedFindingClasses.setEditable(true);
        _tvAllowedFindingClasses.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        _tcFindingShow.setEditable(true);
        _tcFindingShow.setCellFactory(cellData -> new CheckBoxTableCell<>());
        _tcFindingShow.setCellValueFactory(cellData -> cellData.getValue().isCheckedProperty());
        _tcFindingName.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
    }

    private void fillTableViews() {
        if (_trainData == null || !_trainData.isValid()) {
            return;
        }

        var records = _trainData.getRecordsAll();

        var radIdRows = records.stream().map(AbnormalityRecord::getRadId).distinct().sorted()
                .map(radId -> new NamedFlag(radId, true)).toList();
        var findingClassesRows = records.stream().map(AbnormalityRecord::getAbnormalityClass).distinct().sorted()
                .map(fc -> new NamedFlag(fc.getClassName(), true)).toList();

        _tvAllowedRadiologists.getItems().clear();
        _tvAllowedRadiologists.getItems().addAll(radIdRows);

        _tvAllowedFindingClasses.getItems().clear();
        _tvAllowedFindingClasses.getItems().addAll(findingClassesRows);
    }
}
