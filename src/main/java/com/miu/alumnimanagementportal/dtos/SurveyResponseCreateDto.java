package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyResponseCreateDto {

    /**
     * Logical respondent identifier (e.g. email).
     * For anonymous surveys this is used only to build an anonymous hash.
     */
    @NotBlank
    private String respondentKey;

    @Valid
    @NotEmpty
    private List<SurveyAnswerRequestDto> answers = new ArrayList<>();
}


