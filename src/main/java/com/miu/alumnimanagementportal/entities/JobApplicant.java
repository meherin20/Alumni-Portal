package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class JobApplicant extends BaseEntity {

    private String startYear;
    private String designation;
    private String companyName;
    private String endYear;

    //relation onetoone
}
