package com.turtlesamigo.utils;

import com.turtlesamigo.collections.CollectionUtils;
import com.turtlesamigo.model.AbnormalityRecord;
import com.turtlesamigo.model.parsers.AbnormalityRecordFileParser;
import javafx.scene.control.Alert;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.turtlesamigo.utils.UIUtils.showAlert;

public class ParsingUtils {
    /**
     * Read all abnormality records from csv files.
     * @param recordFiles list of csv files with abnormality records
     * @return list of abnormality records from the csv files
     */
    public static List<AbnormalityRecord> readRecordsFromFiles(List<File> recordFiles) {
        var recordsAll = new ArrayList<AbnormalityRecord>();

        if (CollectionUtils.isNullOrEmpty(recordFiles)) {
            return recordsAll;
        }

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
            showAlert("Warning", "Failed to parse some record files.",
                    "Some record files are not parsed correctly. Please check the console for details.",
                    Alert.AlertType.WARNING);
        }

        return recordsAll;
    }

    public static boolean isImageFile(File file) {
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
