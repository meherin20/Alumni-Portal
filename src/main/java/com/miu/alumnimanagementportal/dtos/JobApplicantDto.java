package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.JobApplicant}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplicantDto implements Serializable {
    private Long id;
    private Long version;
    private Date createdDate;
    private Date lastModifiedDate;
    private String startYear;
    @NotNull
    @NotEmpty
    @NotBlank
    private String designation;
    @NotNull
    @NotEmpty
    @NotBlank
    private String companyName;
    @NotNull
    @NotEmpty
    @NotBlank
    private String endYear;
}