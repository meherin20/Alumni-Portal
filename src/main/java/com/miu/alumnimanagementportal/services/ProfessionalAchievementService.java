package com.miu.alumnimanagementportal.services;


import com.miu.alumnimanagementportal.dtos.ProfessionalAchievementDto;

import java.util.List;

public interface ProfessionalAchievementService {
    ProfessionalAchievementDto create(ProfessionalAchievementDto professionalAchievementDto);

    List<ProfessionalAchievementDto> findAll();

    ProfessionalAchievementDto update(ProfessionalAchievementDto professionalAchievementDto, Long id);

    ProfessionalAchievementDto getProfessionalAchievementById(Long id);

    void delete(Long id);
}
