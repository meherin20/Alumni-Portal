package com.miu.alumnimanagementportal.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveyRespondedDto {
    private Long surveyId;
    private String title;
    private String description;
    private LocalDateTime submittedAt;
}

