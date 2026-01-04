package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.WorkExperienceDto;
import com.miu.alumnimanagementportal.entities.WorkExperience;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.WorkExperienceRepository;
import com.miu.alumnimanagementportal.services.WorkExperienceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class WorkExperienceServiceImpl implements WorkExperienceService {
    private final WorkExperienceRepository repository;
    private final Converter converter;
    @Override
    public WorkExperienceDto create(WorkExperienceDto workExperienceDto) {
        Optional.ofNullable(workExperienceDto.getId()).ifPresent(id -> {
            if (repository.existsById(id)) {
                throw new DataAlreadyExistException("Work Experience with id " + id + " already exists");
            }
        });
        return converter.convert(repository.save(converter.convert(workExperienceDto, WorkExperience.class)), WorkExperienceDto.class);
    }

    @Override
    public List<WorkExperienceDto> findAll() {
        return converter.convertList(repository.findAll(), WorkExperienceDto.class);
    }

    @Override
    public WorkExperienceDto update(WorkExperienceDto workExperienceDto, Long id) {
        return Optional.ofNullable(workExperienceDto.getId()).map(entityId -> {
            if (!repository.existsById(entityId)) {
                throw new ResourceNotFoundException("Work Experience with id " + entityId + " not found");
            }
            return converter.convert(
                    repository.save(
                            converter.convert(workExperienceDto, WorkExperience.class)
                    ), WorkExperienceDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Work Experience with id " + id + " not found"));
    }

    @Override
    public WorkExperienceDto getWorkExperienceById(Long id) {
        return Optional.ofNullable(id)
                .map(repository::findById)
                .map(workExperience -> converter.convert(workExperience, WorkExperienceDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Work Experience with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(id).ifPresent(repository::deleteById);
    }
}
