package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "address")
public class Address extends BaseEntity {
    private String city;
    private String state;
    private String country;
    private String zip;
    private String street;

    @ManyToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

}