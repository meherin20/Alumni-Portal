package com.miu.alumnimanagementportal.surveys.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyCreateRequest {
    @NotBlank
    private String title;

    private String description;

    private java.time.LocalDateTime startAt;

    private java.time.LocalDateTime endAt;

    @Valid
    @NotEmpty
    private List<SurveyQuestionRequest> questions = new ArrayList<>();
}
