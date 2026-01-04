package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.miu.alumnimanagementportal.common.enums.JobType;
import com.miu.alumnimanagementportal.entities.PostStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.JobPost}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobPostDto implements Serializable {
    private Long id;
    private Long version;
    private Date createdDate;
    private Date lastModifiedDate;
    @NotNull
    @NotBlank
    private String title;
    @NotNull
    @NotBlank
    private String description;
    @NotNull
    private JobType jobType;
    private boolean isPublished = false;
    @NotNull
    private PostStatus status;

    @NotNull
    @NotBlank
    private String location;
    @NotNull
    @NotBlank
    private String companyName;
    @NotNull
    @NotBlank
    private String city;
    @NotNull
    @NotBlank
    private String state;
}