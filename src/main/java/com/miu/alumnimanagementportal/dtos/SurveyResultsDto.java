package com.miu.alumnimanagementportal.dtos;

import lombok.Data;

import java.util.List;

@Data
public class SurveyResultsDto {

    private Long surveyId;
    private String title;
    private long submittedCount;
    private List<SurveyQuestionResultDto> questions;
}


