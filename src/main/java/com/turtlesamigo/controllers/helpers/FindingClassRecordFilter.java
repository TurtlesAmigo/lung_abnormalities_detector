package com.turtlesamigo.controllers.helpers;

import com.turtlesamigo.model.AbnormalityClass;
import com.turtlesamigo.model.AbnormalityRecord;

import java.util.Set;

public class FindingClassRecordFilter implements IRecordFilter {
    private final Set<AbnormalityClass> _allowedFindingClasses;

    public FindingClassRecordFilter(Set<AbnormalityClass> allowedFindingClasses) {
        _allowedFindingClasses = allowedFindingClasses;
    }

    @Override
    public boolean isMatching(AbnormalityRecord record) {
        return _allowedFindingClasses.contains(record.getAbnormalityClass());
    }
}
