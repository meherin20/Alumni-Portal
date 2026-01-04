package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.WorkExperience}
 */
@Data
public class WorkExperienceDto implements Serializable {
    Long id;
    Long version;
    Date createdDate;
    Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    String startYear;
    @NotNull
    @NotEmpty
    @NotBlank
    String designation;
    @NotNull
    @NotEmpty
    @NotBlank
    String companyName;
    @NotNull
    @NotEmpty
    @NotBlank
    String endYear;
}