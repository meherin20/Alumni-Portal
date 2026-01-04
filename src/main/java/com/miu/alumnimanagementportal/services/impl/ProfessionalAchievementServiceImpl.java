package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.AddressDto;
import com.miu.alumnimanagementportal.dtos.ProfessionalAchievementDto;
import com.miu.alumnimanagementportal.entities.Address;
import com.miu.alumnimanagementportal.entities.ProfessionalAchievement;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.ProfessionalAchievementRepository;
import com.miu.alumnimanagementportal.services.ProfessionalAchievementService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProfessionalAchievementServiceImpl implements ProfessionalAchievementService {
    private final ProfessionalAchievementRepository repository;
    private final Converter converter;
    @Override
    public ProfessionalAchievementDto create(ProfessionalAchievementDto professionalAchievementDto) {
        Optional.ofNullable(professionalAchievementDto.getId()).ifPresent(id -> {
            if (repository.existsById(id)) {
                throw new DataAlreadyExistException("Professional Achievement with id " + id + " already exists");
            }
        });
        return converter.convert(repository.save(converter.convert(professionalAchievementDto, ProfessionalAchievement.class)), ProfessionalAchievementDto.class);
    }

    @Override
    public List<ProfessionalAchievementDto> findAll() {
        return converter.convertList(repository.findAll(), ProfessionalAchievementDto.class);
    }

    @Override
    public ProfessionalAchievementDto update(ProfessionalAchievementDto professionalAchievementDto, Long id) {
        return Optional.ofNullable(professionalAchievementDto.getId()).map(entityId -> {
            if (!repository.existsById(entityId)) {
                throw new ResourceNotFoundException("Professional Achievement with id " + entityId + " not found");
            }
            return converter.convert(
                    repository.save(
                            converter.convert(professionalAchievementDto, ProfessionalAchievement.class)
                    ), ProfessionalAchievementDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Professional Achievement with id " + id + " not found"));
    }

    @Override
    public ProfessionalAchievementDto getProfessionalAchievementById(Long id) {
        return Optional.ofNullable(id)
                .map(repository::findById)
                .map(professionalAchievement -> converter.convert(professionalAchievement, ProfessionalAchievementDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Professional Achievement with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(id).ifPresent(repository::deleteById);
    }
}
