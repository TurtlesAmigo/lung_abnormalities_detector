package com.turtlesamigo.model.parsers;

import com.turtlesamigo.model.AbnormalityClass;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opencv.core.Point;
import org.opencv.core.Rect2d;

class AbnormalityRecordFileParserTest {

    @Test
    @DisplayName("Abnormality Record Parsing Test")
    void abnormalityRecordParsingTest() throws Exception {
        String testResourcePath = "src/test/resources/record_parser_test.csv";
        AbnormalityRecordFileParser parser = new AbnormalityRecordFileParser(testResourcePath);
        var records = parser.getRecords();

        Assertions.assertEquals(1, records.size());

        var record = records.getFirst();

        Assertions.assertEquals("9a5094b2563a1ef3ff50dc5c7ff71345", record.getImageId());
        Assertions.assertEquals(AbnormalityClass.CARDIOMEGALY, record.getAbnormalityClass());
        Assertions.assertEquals("R10", record.getRadId());
        Assertions.assertEquals(new Rect2d(new Point(691.0,1375.0), new Point(1653.0,1831.0)),
                record.getBoundingBox());
    }
}