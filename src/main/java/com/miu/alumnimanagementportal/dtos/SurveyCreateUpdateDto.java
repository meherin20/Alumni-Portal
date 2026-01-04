package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyCreateUpdateDto {

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private Boolean anonymous;

    private LocalDateTime publishAt;
    private LocalDateTime closeAt;

    private String targetDepartment;
    private Integer targetGraduationYear;
    private String targetCountry;

    @Valid
    @NotEmpty
    private List<SurveyQuestionDto> questions = new ArrayList<>();
}


