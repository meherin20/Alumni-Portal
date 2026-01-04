package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.surveys.enums.SurveyStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyDetailDto {

    private Long id;
    private String title;
    private String description;
    private SurveyStatus status;
    private boolean anonymous;
    private LocalDateTime publishAt;
    private LocalDateTime closeAt;
    private String targetDepartment;
    private Integer targetGraduationYear;
    private String targetCountry;

    private List<SurveyQuestionDto> questions = new ArrayList<>();
}


