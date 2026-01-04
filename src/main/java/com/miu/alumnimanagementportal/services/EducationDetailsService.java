package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.EducationDetailsDto;
import com.miu.alumnimanagementportal.dtos.EducationDetailsDto;

import java.util.List;

public interface EducationDetailsService {

    void create(EducationDetailsDto educationDetailsDto);

    List<EducationDetailsDto> findAll();

    EducationDetailsDto update(EducationDetailsDto educationDetailsDto, Long id);

    EducationDetailsDto getEducationDetailsById(Long id);

    void delete(Long id);
}
