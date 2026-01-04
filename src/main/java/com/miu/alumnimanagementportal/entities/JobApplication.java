package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class JobApplication extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "job_post_id")
    private JobPost jobPost;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private User applicant;

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false)
    private String email;

    private String phoneNumber;

    private String currentWorkplace;

    private String portfolioLink;

    private String githubLink;

    @Column(length = 1000)
    private String experience;

    @Column(length = 2000)
    private String coverLetter;

    @Column(length = 500)
    private String skills;

    private String resumeFileName;

    private String photoFileName;

    @Enumerated(EnumType.STRING)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    public enum ApplicationStatus {
        PENDING,
        REVIEWED,
        ACCEPTED,
        REJECTED
    }
}
