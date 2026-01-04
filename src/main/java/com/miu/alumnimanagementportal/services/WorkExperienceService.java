package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.WorkExperienceDto;

import java.util.List;

public interface WorkExperienceService {
    WorkExperienceDto create(WorkExperienceDto workExperienceDto);

    List<WorkExperienceDto> findAll();

    WorkExperienceDto update(WorkExperienceDto workExperienceDto, Long id);

    WorkExperienceDto getWorkExperienceById(Long id);

    void delete(Long id);
}
