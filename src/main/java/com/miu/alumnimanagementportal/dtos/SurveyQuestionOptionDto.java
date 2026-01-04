package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SurveyQuestionOptionDto {

    private Long id;

    @NotBlank
    private String label;

    @NotBlank
    private String value;

    @NotNull
    private Integer orderIndex;
}


