package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.EducationDetailsDto;
import com.miu.alumnimanagementportal.dtos.RoleDto;
import com.miu.alumnimanagementportal.dtos.SurveyDto;
import com.miu.alumnimanagementportal.entities.Role;
import com.miu.alumnimanagementportal.entities.Survey;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.SurveyRepository;
import com.miu.alumnimanagementportal.services.SurveyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SurveyServiceImpl implements SurveyService {
    private final SurveyRepository repository;
    private final Converter converter;
    @Override
    public SurveyDto create(SurveyDto surveyDto) {
        Optional.ofNullable(surveyDto.getId()).ifPresent(id -> {
            if (repository.existsById(id)) {
                throw new DataAlreadyExistException("Survey with id " + id + " already exists");
            }
        });
        return converter.convert(repository.save(converter.convert(surveyDto, Survey.class)), SurveyDto.class);
    }

    @Override
    public List<SurveyDto> findAll() {
       return converter.convertList(repository.findAll(), SurveyDto.class);
    }

    @Override
    public SurveyDto update(SurveyDto surveyDto, Long id) {
        return Optional.ofNullable(surveyDto.getId()).map(entityId -> {
            if (!repository.existsById(entityId)) {
                throw new ResourceNotFoundException("Survey with id " + entityId + " not found");
            }
            return converter.convert(
                    repository.save(
                            converter.convert(surveyDto, Survey.class)
                    ), SurveyDto.class);
        }).map(survey -> converter.convert(survey, SurveyDto.class)).orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
    }

    @Override
    public SurveyDto getSurveyById(Long id) {
        return Optional.ofNullable(id)
                .map(repository::findById)
                .map(survey -> converter.convert(survey, SurveyDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(id).ifPresent(repository::deleteById);
    }
}
