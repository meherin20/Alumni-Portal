package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.AddressDto;
import com.miu.alumnimanagementportal.dtos.AttendantDto;
import com.miu.alumnimanagementportal.entities.Address;
import com.miu.alumnimanagementportal.entities.Attendant;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.AddressRepository;
import com.miu.alumnimanagementportal.repositories.AttendantRepository;
import com.miu.alumnimanagementportal.services.AttendantService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AttendantServiceImpl implements AttendantService {

    private final AttendantRepository attendantRepository;
    private final Converter converter;


    @Override
    public void create(AttendantDto attendantDto) {
        Optional.ofNullable(attendantDto.getId()).ifPresent(id -> {
            if (attendantRepository.existsById(id)) {
                throw new DataAlreadyExistException("Attendant with id " + id + " already exists");
            }
        });
        attendantRepository.save(converter.convert(attendantDto, Attendant.class));
    }

    @Override
    public List<AttendantDto> findAll() {
        return attendantRepository.findAll().stream()
                .map(element -> converter.convert(element, AttendantDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public AttendantDto update(AttendantDto attendantDto, Long id) {
        return Optional.ofNullable(attendantDto.getId()).map(entityId -> {
            if (!attendantRepository.existsById(entityId)) {
                throw new ResourceNotFoundException("Attendant with id " + entityId + " not found");
            }
            return converter.convert(attendantRepository.save(converter.convert(attendantDto, Attendant.class)), AttendantDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Attendant with id " + id + " not found"));
    }

    @Override
    public AttendantDto getAttendantById(Long id) {
        return Optional.ofNullable(id)
                .map(attendantRepository::findById)
                .map(element -> converter.convert(element, AttendantDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Attendant with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        if (!attendantRepository.existsById(id)) {
            throw new ResourceNotFoundException("Attendent with id " + id + " not found");
        }
        attendantRepository.deleteById(id);

    }
}
