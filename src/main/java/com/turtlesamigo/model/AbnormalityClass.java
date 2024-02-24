package com.turtlesamigo.model;

import javafx.scene.paint.Color;

public enum AbnormalityClass {
    AORTIC_ENLARGEMENT  (0, "Aortic enlargement", Color.RED),
    ATELECTASIS         (1, "Atelectasis", Color.BLUE),
    CALCIFICATION       (2, "Calcification", Color.GREEN),
    CARDIOMEGALY        (3, "Cardiomegaly", Color.YELLOW),
    CONSOLIDATION       (4, "Consolidation", Color.ORANGE),
    ILD                 (5, "ILD", Color.PURPLE),
    INFILTRATION        (6, "Infiltration", Color.PINK),
    LUNG_OPACITY        (7, "Lung opacity", Color.CYAN),
    NODULE_MASS         (8, "Nodule/Mass", Color.BROWN),
    OTHER_LESION        (9, "Other lesion", Color.GRAY),
    PLEURAL_EFFUSION    (10, "Pleural effusion", Color.LIGHTGRAY),
    PLEURAL_THICKENING  (11, "Pleural thickening", Color.LIGHTPINK),
    PNEUMOTHORAX        (12, "Pneumothorax", Color.LIGHTBLUE),
    PULMONARY_FIBROSIS  (13, "Pulmonary fibrosis", Color.LIGHTGREEN),
    NO_FINDING          (14, "No finding", Color.WHITE);

    private final int _classId;
    private final String _className;
    private final Color _color;

    AbnormalityClass(int classId, String className, Color color) {
        _classId = classId;
        _className = className;
        _color = color;
    }

    public int getClassId() {
        return _classId;
    }

    public String getClassName() {
        return _className;
    }

    public boolean isFinding() {
        return this != NO_FINDING;
    }

    public Color getColor() {
        return _color;
    }

    public static AbnormalityClass getClassById(int id) {
        return switch (id) {
            case 0 -> AORTIC_ENLARGEMENT;
            case 1 -> ATELECTASIS;
            case 2 -> CALCIFICATION;
            case 3 -> CARDIOMEGALY;
            case 4 -> CONSOLIDATION;
            case 5 -> ILD;
            case 6 -> INFILTRATION;
            case 7 -> LUNG_OPACITY;
            case 8 -> NODULE_MASS;
            case 9 -> OTHER_LESION;
            case 10 -> PLEURAL_EFFUSION;
            case 11 -> PLEURAL_THICKENING;
            case 12 -> PNEUMOTHORAX;
            case 13 -> PULMONARY_FIBROSIS;
            default -> NO_FINDING;
        };
    }

    @Override
    public String toString() {
        return "[" + _classId + " - " + _className + "]";
    }
}
