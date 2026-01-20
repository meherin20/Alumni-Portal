package com.miu.alumnimanagementportal.surveys.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum SurveyQuestionType {
    MCQ,
    CHECKBOX,
    SHORT_TEXT,
    LONG_TEXT,
    RATING;

    @JsonValue
    public String getValue() {
        return name();
    }

    @JsonCreator
    public static SurveyQuestionType fromValue(String value) {
        return SurveyQuestionType.valueOf(value);
    }
}
