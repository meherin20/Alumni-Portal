package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.EducationDetailsDto;
import com.miu.alumnimanagementportal.dtos.JobApplicantDto;

import java.util.List;

public interface JobApplicantService {
    void create(JobApplicantDto jobApplicantDto);

    List<JobApplicantDto> findAll();

    JobApplicantDto update(JobApplicantDto jobApplicantDto, Long id);

    JobApplicantDto getJobApplicantById(Long id);

    void delete(Long id);
}
