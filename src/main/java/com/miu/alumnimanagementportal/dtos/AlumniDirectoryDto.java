package com.miu.alumnimanagementportal.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AlumniDirectoryDto implements Serializable {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String profileImage;
    private String department;
    private String passingYear;
    private String jobTitle;
    private String company;
    private String phone;
    private String cvLink;
    private String portfolioLink;
    private AddressDto address;
    private List<WorkExperienceDto> workExperiences;
    private List<ProfessionalAchievementDto> professionalAchievements;
    private List<EducationDetailsDto> educationDetails;
}

