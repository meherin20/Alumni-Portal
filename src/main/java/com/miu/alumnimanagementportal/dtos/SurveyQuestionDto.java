package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.surveys.enums.QuestionType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyQuestionDto {

    private Long id;

    @NotBlank
    private String text;

    @NotNull
    private QuestionType type;

    @NotNull
    private Boolean required;

    @NotNull
    private Integer orderIndex;

    @Valid
    private List<SurveyQuestionOptionDto> options = new ArrayList<>();
}


