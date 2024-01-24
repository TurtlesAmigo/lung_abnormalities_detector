package com.turtlesamigo.model;

public enum AbnormalityClass {
    AORTIC_ENLARGEMENT  (0, "Aortic enlargement"),
    ATELECTASIS         (1, "Atelectasis"),
    CALCIFICATION       (2, "Calcification"),
    CARDIOMEGALY        (3, "Cardiomegaly"),
    CONSOLIDATION       (4, "Consolidation"),
    ILD                 (5, "ILD"),
    INFILTRATION        (6, "Infiltration"),
    LUNG_OPACITY        (7, "Lung opacity"),
    NODULE_MASS         (8, "Nodule/Mass"),
    OTHER_LESION        (9, "Other lesion"),
    PLEURAL_EFFUSION    (10, "Pleural effusion"),
    PLEURAL_THICKENING  (11, "Pleural thickening"),
    PNEUMOTHORAX        (12, "Pneumothorax"),
    PULMONARY_FIBROSIS  (13, "Pulmonary fibrosis"),
    NO_FINDING          (14, "No finding");

    private final int _classId;
    private final String _className;

    AbnormalityClass(int classId, String className) {
        _classId = classId;
        _className = className;
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
