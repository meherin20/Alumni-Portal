package com.miu.alumnimanagementportal.surveys.dtos;

import com.miu.alumnimanagementportal.surveys.enums.SurveyQuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyQuestionRequest {
    @NotBlank
    private String questionText;

    @NotNull
    private SurveyQuestionType questionType;

    @NotNull
    private Boolean required;

    @Valid
    private List<SurveyOptionRequest> options = new ArrayList<>();
}
