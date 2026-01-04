package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.AttendantDto;
import com.miu.alumnimanagementportal.dtos.EducationDetailsDto;
import com.miu.alumnimanagementportal.entities.Attendant;
import com.miu.alumnimanagementportal.entities.EducationDetails;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.AttendantRepository;
import com.miu.alumnimanagementportal.repositories.EducationDetailsRepository;
import com.miu.alumnimanagementportal.services.EducationDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EducationDetailsServiceImpl implements EducationDetailsService {

    private final EducationDetailsRepository educationDetailsRepository;
    private final Converter converter;

    @Override
    public void create(EducationDetailsDto educationDetailsDto) {
        Optional.ofNullable(educationDetailsDto.getId()).ifPresent(id -> {
            if (educationDetailsRepository.existsById(id)) {
                throw new DataAlreadyExistException("EducationDetails with id " + id + " already exists");
            }
        });
        educationDetailsRepository.save(converter.convert(educationDetailsDto, EducationDetails.class));
    }


    @Override
    public List<EducationDetailsDto> findAll() {
        return educationDetailsRepository.findAll().stream()
                .map(element -> converter.convert(element, EducationDetailsDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public EducationDetailsDto update(EducationDetailsDto educationDetailsDto, Long id) {
        return Optional.ofNullable(educationDetailsDto.getId()).map(entityId -> {
            if (!educationDetailsRepository.existsById(entityId)) {
                throw new ResourceNotFoundException("EducationDetails with id " + entityId + " not found");
            }
           return converter.convert(educationDetailsRepository.save(converter.convert(educationDetailsDto, EducationDetails.class)), EducationDetailsDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("EducationDetails with id " + id + " not found"));
    }

    @Override
    public EducationDetailsDto getEducationDetailsById(Long id) {
        return Optional.ofNullable(id)
                .map(educationDetailsRepository::findById)
                .map(element -> converter.convert(element, EducationDetailsDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("EducationDetails with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        if (!educationDetailsRepository.existsById(id)) {
            throw new ResourceNotFoundException("EducationDetails with id " + id + " not found");
        }
        educationDetailsRepository.deleteById(id);
    }
}
