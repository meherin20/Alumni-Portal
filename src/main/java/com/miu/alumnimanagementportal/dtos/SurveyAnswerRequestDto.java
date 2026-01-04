package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SurveyAnswerRequestDto {

    @NotNull
    private Long questionId;

    private String choiceValue;
    private String multiChoiceValues;
    private Integer ratingValue;
    private String textValue;
}


