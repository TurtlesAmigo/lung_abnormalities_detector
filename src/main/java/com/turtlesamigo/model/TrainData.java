package com.turtlesamigo.model;

import com.turtlesamigo.collections.CollectionUtils;
import com.turtlesamigo.collections.MapOfLists;
import com.turtlesamigo.controllers.helpers.IRecordFilter;
import com.turtlesamigo.utils.ParsingUtils;

import java.io.File;
import java.util.*;

import static com.turtlesamigo.utils.ParsingUtils.readRecordsFromFiles;

/**
 * A manager for the training data.
 * It is used to load the records from the file system and
 * to provide access to the records.
 */
public class TrainData {
    private final File _trainDirectory;

    private final List<AbnormalityRecord> _recordsAll;
    private final MapOfLists<String, AbnormalityRecord> _imageId2Records;
    private final Map<String, File> _imageId2File = new HashMap<>();

    private final boolean _isValid;

    public TrainData(File trainDirectory) {
        _trainDirectory = trainDirectory;

        List<File> _recordFiles = _trainDirectory == null ? null
                : Arrays.stream(Objects.requireNonNull(_trainDirectory.listFiles((dir, name) -> name.endsWith(".csv"))))
                .toList();

        _recordsAll = readRecordsFromFiles(_recordFiles);

        _imageId2Records = new MapOfLists<>();
        for (var record : _recordsAll) {
            _imageId2Records.add(record.getImageId(), record);
        }

        buildImage2FileMap();

        _isValid = _trainDirectory != null && !CollectionUtils.isNullOrEmpty(_recordsAll)
                && !CollectionUtils.isNullOrEmpty(_imageId2File.keySet());
    }

    public List<AbnormalityRecord> getRecordsAll() {
        return _recordsAll;
    }

    public MapOfLists<String, AbnormalityRecord> getImageId2Records() {
        return _imageId2Records;
    }

    public Map<String, File> getImageId2File() {
        return _imageId2File;
    }

    public File getTrainDirectory() {
        return _trainDirectory;
    }

    public boolean isValid() {
        return _isValid;
    }

    private void buildImage2FileMap() {
        if (_trainDirectory == null) {
            return;
        }

        var imageFiles = _trainDirectory.listFiles(ParsingUtils::isImageFile);

        if (imageFiles == null) {
            return;
        }

        _imageId2File.clear();
        for (var imageFile : imageFiles) {
            var imageId = imageFile.getName().split("\\.")[0];
            _imageId2File.put(imageId, imageFile);
        }
    }

    private TrainData(File trainDirectory, List<AbnormalityRecord> recordsAll) {
        _trainDirectory = trainDirectory;
        _recordsAll = recordsAll;

        _imageId2Records = new MapOfLists<>();
        for (var record : _recordsAll) {
            _imageId2Records.add(record.getImageId(), record);
        }

        buildImage2FileMap();

        _isValid = _trainDirectory != null && !CollectionUtils.isNullOrEmpty(_recordsAll)
                && !CollectionUtils.isNullOrEmpty(_imageId2File.keySet());
    }

    public static TrainData filterTrainData(TrainData trainData, IRecordFilter ... recordFilters) {
        if (trainData == null || !trainData.isValid()) {
            return trainData;
        }

        var recordsAll = trainData.getRecordsAll();
        var filteredRecords = recordsAll.stream().filter(record -> {
            for (var recordFilter : recordFilters) {
                if (!recordFilter.isMatching(record)) {
                    return false;
                }
            }
            return true;
        }).toList();

        return new TrainData(trainData.getTrainDirectory(), filteredRecords);
    }
}
