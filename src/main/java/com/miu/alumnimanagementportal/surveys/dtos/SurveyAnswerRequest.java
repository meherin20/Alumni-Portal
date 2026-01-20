package com.miu.alumnimanagementportal.surveys.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SurveyAnswerRequest {
    @NotNull
    private Long questionId;

    @NotBlank
    private String answerValue;
}
