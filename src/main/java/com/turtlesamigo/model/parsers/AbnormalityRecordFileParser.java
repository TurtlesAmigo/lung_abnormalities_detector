package com.turtlesamigo.model.parsers;

import com.opencsv.CSVReader;
import com.turtlesamigo.model.AbnormalityClass;
import com.turtlesamigo.model.AbnormalityRecord;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;

import java.io.FileReader;
import java.util.List;
import java.util.stream.Collectors;

public class AbnormalityRecordFileParser {
    private final String _filePath;
    private final List<AbnormalityRecord> _records;

    public AbnormalityRecordFileParser(String csvPath) throws Exception {
        _filePath = csvPath;
        FileReader fileReader = new FileReader(csvPath);
        CSVReader csvReader = new CSVReader(fileReader);
        var rows = csvReader.readAll();
        _records = rows.stream().map(this::parseCsvRow).collect(Collectors.toList());
    }

    public String getFilePath() {
        return _filePath;
    }

    public List<AbnormalityRecord> getRecords() {
        return _records;
    }

    private AbnormalityRecord parseCsvRow(String[] row) {
        assert row.length == 8;

        var abnormalityClass = parseAbnormalityClass(row);
        var rect = parseRect(row);

        return new AbnormalityRecord(row[0], abnormalityClass, row[3], rect);
    }

    private static Rect2d parseRect(String[] row) {
        if (row.length < 3) {
            return null;
        }

        if (Integer.parseInt(row[2]) == AbnormalityClass.NO_FINDING.getClassId()) {
            return null;
        }

        double minX = Double.parseDouble(row[4]);
        double minY = Double.parseDouble(row[5]);

        double maxX = Double.parseDouble(row[6]);
        double maxY = Double.parseDouble(row[7]);

        Point minP = new Point(minX, minY);
        Point maxP = new Point(maxX, maxY);

        return new Rect2d(minP, maxP);
    }

    private static AbnormalityClass parseAbnormalityClass(String[] row) {
        return row.length < 3 ? AbnormalityClass.NO_FINDING : AbnormalityClass.getClassById(Integer.parseInt(row[2]));
    }
}
