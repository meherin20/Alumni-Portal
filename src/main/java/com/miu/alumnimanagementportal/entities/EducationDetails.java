package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class EducationDetails extends BaseEntity {
    private String institutionName;
    private String degree;
    @Size(min = 4, max = 4)
    private String passingYear;
    private String cgpa;
    private String course;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

}
