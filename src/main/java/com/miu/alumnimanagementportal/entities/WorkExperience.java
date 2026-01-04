package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class WorkExperience extends BaseEntity {

    private String startYear;
    private String designation;
    private String companyName;
    private String endYear;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

// relation
}
