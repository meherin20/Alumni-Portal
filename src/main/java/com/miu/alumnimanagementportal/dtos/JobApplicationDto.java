package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class JobApplicationDto implements Serializable {
    private Long id;
    private Long version;
    private Date createdDate;
    private Date lastModifiedDate;

    private Long jobPostId;
    private String jobTitle; // For display purposes
    private String jobCompany; // For display purposes

    private Long applicantId;
    private String applicantName; // For display purposes

    @NotNull
    @NotBlank
    @NotEmpty
    private String fullName;

    @NotNull
    @Email
    private String email;

    private String phoneNumber;
    private String currentWorkplace;
    private String portfolioLink;
    private String githubLink;

    private String experience;
    private String coverLetter;
    private String skills;

    private String resumeFileName;
    private String photoFileName;

    private String status;

    // For file uploads (not persisted)
    private transient String photoFile;
    private transient String resumeFile;
}
