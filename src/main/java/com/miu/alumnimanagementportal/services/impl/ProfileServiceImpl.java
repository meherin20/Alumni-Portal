package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.ProfessionalAchievementDto;
import com.miu.alumnimanagementportal.dtos.ProfileDto;
import com.miu.alumnimanagementportal.entities.ProfessionalAchievement;
import com.miu.alumnimanagementportal.entities.Profile;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.ProfileRepository;
import com.miu.alumnimanagementportal.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository repository;
    private final Converter converter;
    @Override
    public ProfileDto create(ProfileDto profileDto) {
        Optional.ofNullable(profileDto.getId()).ifPresent(id -> {
            if (repository.existsById(id)) {
                throw new DataAlreadyExistException("Profile with id " + id + " already exists");
            }
        });
        return converter.convert(repository.save(converter.convert(profileDto, Profile.class)), ProfileDto.class);
    }

    @Override
    public List<ProfileDto> findAll() {
        return converter.convertList(repository.findAll(), ProfileDto.class);
    }

    @Override
    public ProfileDto update(ProfileDto profileDto, Long id) {
        return Optional.ofNullable(profileDto.getId()).map(entityId -> {
            if (!repository.existsById(entityId)) {
                throw new ResourceNotFoundException("Profile with id " + entityId + " not found");
            }
            return converter.convert(
                    repository.save(
                            converter.convert(profileDto, Profile.class)
                    ), ProfileDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Profile with id " + id + " not found"));
    }

    @Override
    public ProfileDto getProfileById(Long id) {
        return Optional.ofNullable(id)
                .map(repository::findById)
                .map(profile -> converter.convert(profile, ProfileDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Profile information with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(id).ifPresent(repository::deleteById);
    }
}
