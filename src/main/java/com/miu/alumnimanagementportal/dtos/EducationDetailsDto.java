package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.EducationDetails}
 */
@Data
public class EducationDetailsDto implements Serializable {
    Long id;
    Long version;
    Date createdDate;
    Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    String institutionName;
    @NotNull
    @NotEmpty
    @NotBlank
    String degree;
    @Size(min = 4, max = 4)
    String passingYear;
    @NotNull
    @NotEmpty
    @NotBlank
    String cgpa;
    @NotNull
    @NotEmpty
    @NotBlank
    String course;
}