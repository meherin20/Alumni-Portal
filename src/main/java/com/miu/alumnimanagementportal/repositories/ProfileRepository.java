package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileRepository extends JpaRepository<Profile, Long> {
}