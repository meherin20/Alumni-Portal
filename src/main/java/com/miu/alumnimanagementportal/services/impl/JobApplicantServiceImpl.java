package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.EventDto;
import com.miu.alumnimanagementportal.dtos.JobApplicantDto;
import com.miu.alumnimanagementportal.entities.Attendant;
import com.miu.alumnimanagementportal.entities.Event;
import com.miu.alumnimanagementportal.entities.JobApplicant;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.EventRepository;
import com.miu.alumnimanagementportal.repositories.JobApplicantRepository;
import com.miu.alumnimanagementportal.services.JobApplicantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JobApplicantServiceImpl implements JobApplicantService {

    private final JobApplicantRepository jobApplicantRepository;
    private final Converter converter;
    private final ModelMapper modelMapper;

    @Override
    public void create(JobApplicantDto jobApplicantDto) {
        Optional.ofNullable(jobApplicantDto.getId()).ifPresent(id -> {
            if (jobApplicantRepository.existsById(id)) {
                throw new DataAlreadyExistException("JobApplication with id " + id + " already exists");
            }
        });
        jobApplicantRepository.save(converter.convert(jobApplicantDto, JobApplicant.class));
    }


    @Override
    public List<JobApplicantDto> findAll() {
        return jobApplicantRepository.findAll()
                .stream()
                .map((element) -> modelMapper.map(element, JobApplicantDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicantDto update(JobApplicantDto jobApplicantDto, Long id) {
        return Optional.ofNullable(jobApplicantDto.getId()).map(entityId -> {
            if (!jobApplicantRepository.existsById(entityId)) {
                throw new ResourceNotFoundException("Event with id " + entityId + " not found");
            }
            return converter.convert(jobApplicantRepository.save(converter.convert(jobApplicantDto, JobApplicant.class)), JobApplicantDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    public JobApplicantDto getJobApplicantById(Long id) {
        return Optional.ofNullable(id)
                .map(jobApplicantRepository::findById)
                .map(element -> converter.convert(element, JobApplicantDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("JobApplication with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        if (!jobApplicantRepository.existsById(id)) {
            throw new ResourceNotFoundException("JobApplication with id " + id + " not found");
        }
        jobApplicantRepository.deleteById(id);
    }
}
