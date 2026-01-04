package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
public class Profile extends BaseEntity {
    @OneToOne
    private User user;
    @OneToOne
    @JoinColumn(name = "address_id")
    private Address address;
    private String phone;
    private String profileImage;

    private String department;
    private String passingYear;
    private String jobTitle;
    private String cvLink;
    private String portfolioLink;

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<WorkExperience> workExperiences = new LinkedHashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ProfessionalAchievement> professionalAchievements = new LinkedHashSet<>();

    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<EducationDetails> educationDetails = new LinkedHashSet<>();

}
