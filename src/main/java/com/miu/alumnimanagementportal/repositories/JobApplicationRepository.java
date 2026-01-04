package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByJobPostId(Long jobPostId);
    List<JobApplication> findByApplicantId(Long applicantId);
    boolean existsByJobPostIdAndApplicantId(Long jobPostId, Long applicantId);
}
