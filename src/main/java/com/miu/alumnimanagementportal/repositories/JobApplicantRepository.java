package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.JobApplicant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobApplicantRepository extends JpaRepository<JobApplicant, Long> {
}