package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.JobPostDto;
import com.miu.alumnimanagementportal.dtos.JobPostsByFilterDto;

import java.util.List;

public interface JobPostService {
    void create(JobPostDto jobPostDto);

    List<JobPostDto> findAll();

    JobPostDto update(JobPostDto jobPostDto, Long id);

    JobPostDto getjobPostById(Long id);

    void delete(Long id);

    List<JobPostDto>  getJobPostsByFilter(JobPostsByFilterDto jobPostsByFilterDto);

    List<JobPostDto> findPublishedJobs();

    void publishJob(Long id);

    void unpublishJob(Long id);

    void publishAllJobs();
}
