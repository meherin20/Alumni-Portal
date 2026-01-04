package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.JobApplicationDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface JobApplicationService {
    JobApplicationDto create(JobApplicationDto jobApplicationDto, MultipartFile photoFile, MultipartFile resumeFile);

    List<JobApplicationDto> findAll();

    List<JobApplicationDto> findByJobPostId(Long jobPostId);

    List<JobApplicationDto> findByApplicantId(Long applicantId);

    JobApplicationDto getJobApplicationById(Long id);

    JobApplicationDto updateStatus(Long id, String status);

    void delete(Long id);

    boolean hasUserAppliedForJob(Long jobPostId, Long applicantId);
}
