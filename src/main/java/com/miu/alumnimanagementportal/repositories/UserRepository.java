package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByEmail(String email);

    @Query("""
        select u from User u
        join u.profile p
        join p.address a
        join p.educationDetails ed
        join p.workExperiences we
        where ed.passingYear = :graduationYear
          and ed.course = :course
          and a.city = :city
          and a.state = :state
          and we.companyName = :industry
    """)
    List<User> findUsersByFilter(
            @Param("graduationYear") String graduationYear,
            @Param("course") String course,
            @Param("city") String city,
            @Param("state") String state,
            @Param("industry") String industry
    );

}


