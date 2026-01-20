package com.miu.alumnimanagementportal.surveys.dtos;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SurveySubmitRequest {
    @Email
    @NotBlank
    private String userEmail;

    @Valid
    @NotEmpty
    private List<SurveyAnswerRequest> answers = new ArrayList<>();
}
