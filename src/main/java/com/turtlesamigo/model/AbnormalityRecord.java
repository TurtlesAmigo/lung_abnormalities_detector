package com.turtlesamigo.model;

import org.opencv.core.Rect2d;

public class AbnormalityRecord {
    public static final String UNKNOWN_RAD_ID_STRING = "Unknown";

    private final String _imageId;
    private final AbnormalityClass _abnormalityClass;
    private final String _radId;
    private final Rect2d _boundingBox;

    public AbnormalityRecord(String imageId,
                             AbnormalityClass abnormalityClass,
                             String radId,
                             Rect2d boundingBox) {
        _imageId = imageId;
        _abnormalityClass = abnormalityClass;
        _radId = radId == null ? UNKNOWN_RAD_ID_STRING : radId;
        _boundingBox = boundingBox;

        // The bounding box should be null when there is no finding.
        assert _abnormalityClass.isFinding() == (_boundingBox != null);
    }

    public String getImageId() {
        return _imageId;
    }

    public AbnormalityClass getAbnormalityClass() {
        return _abnormalityClass;
    }

    public String getRadId() {
        return _radId;
    }

    public Rect2d getBoundingBox() {
        return _boundingBox;
    }
}
