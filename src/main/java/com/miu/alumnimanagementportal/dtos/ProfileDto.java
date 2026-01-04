package com.miu.alumnimanagementportal.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Value;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.Profile}
 */
@Data
public class ProfileDto implements Serializable {
    Long id;
    Long version;
    Date createdDate;
    Date lastModifiedDate;
    @NotNull
    @NotEmpty
    @NotBlank
    String phone;

    String profileImage;

    String department;
    String passingYear;
    String jobTitle;
    String cvLink;
    String portfolioLink;

    Set<WorkExperienceDto> workExperiences;

    Set<ProfessionalAchievementDto> professionalAchievements;
    Set<EducationDetailsDto> educationDetails;
}