package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class User extends BaseEntity{

    @Column(name = "firstname", nullable = false)
    private String firstName;
    @Column(name = "lastname", nullable = false)
    private String lastName;

    @Column(unique=true, nullable = false)
    private String email;
    private String password;

    // ONE user = ONE role only (ADMIN, ALUMNI, or STUDENT)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id")
    @org.hibernate.annotations.NotFound(action = org.hibernate.annotations.NotFoundAction.IGNORE)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "profile_id", unique = true)
    private Profile profile;


    private boolean is_active = true;

    private boolean is_locked = false;

    private int loginCount = 0;

    private LocalDateTime lastLockedDateTime;



//    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
//    private Profile profile;

}
