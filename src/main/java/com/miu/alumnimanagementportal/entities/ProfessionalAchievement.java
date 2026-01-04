package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
public class ProfessionalAchievement extends BaseEntity {

    private String title;
    private Date year;
    private String description;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "profile_id", nullable = false)
    private Profile profile;

    //Relation remaining


}
