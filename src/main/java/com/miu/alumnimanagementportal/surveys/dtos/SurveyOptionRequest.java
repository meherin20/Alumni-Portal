package com.miu.alumnimanagementportal.surveys.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SurveyOptionRequest {
    @NotBlank
    private String label;
}
