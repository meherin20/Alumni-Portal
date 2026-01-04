package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.JobPost;
import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobPostRepository extends JpaRepository<JobPost, Long> {
}