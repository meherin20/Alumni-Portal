package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.surveys.enums.SurveyStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveySummaryDto {

    private Long id;
    private String title;
    private String description;
    private SurveyStatus status;
    private LocalDateTime publishAt;
    private LocalDateTime closeAt;
}


