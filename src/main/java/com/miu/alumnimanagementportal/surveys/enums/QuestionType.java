package com.miu.alumnimanagementportal.surveys.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum QuestionType {
    SINGLE_CHOICE,
    MULTI_CHOICE,
    RATING_1_5,
    TEXT,
    YES_NO;

    @JsonValue
    public String getValue() {
        return this.name();
    }

    @JsonCreator
    public static QuestionType fromValue(String value) {
        return QuestionType.valueOf(value);
    }
}


