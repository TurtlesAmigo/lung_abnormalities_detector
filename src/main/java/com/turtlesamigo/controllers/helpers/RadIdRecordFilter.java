package com.turtlesamigo.controllers.helpers;

import com.turtlesamigo.model.AbnormalityRecord;

import java.util.Set;

public class RadIdRecordFilter implements IRecordFilter {
    private final Set<String> _allowedRadIds;

    public RadIdRecordFilter(Set<String> allowedRadIds) {
        _allowedRadIds = allowedRadIds;
    }

    @Override
    public boolean isMatching(AbnormalityRecord record) {
        return _allowedRadIds.contains(record.getRadId());
    }
}
