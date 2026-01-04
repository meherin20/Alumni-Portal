package com.miu.alumnimanagementportal.entities;

import com.miu.alumnimanagementportal.common.enums.JobType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToOne;
import lombok.Data;

@Data
@Entity
public class JobPost extends BaseEntity {

    @Column(length = 255)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;


    private JobType jobType;

    //owner of the job (optional for admin-created jobs)
    @OneToOne
    private User owner;

    private boolean isPublished = false;

    //status of the job - open or closed
    private PostStatus status;

    @Column(length = 255)
    private String location;
    @Column(length = 255)
    private String companyName;
    @Column(length = 255)
    private String city;
    @Column(length = 255)
    private String state;


}
