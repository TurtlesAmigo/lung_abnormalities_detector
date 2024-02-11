package com.turtlesamigo.controllers.helpers;

import com.turtlesamigo.model.AbnormalityRecord;

public interface IRecordFilter {
    boolean isMatching(AbnormalityRecord record);
}
