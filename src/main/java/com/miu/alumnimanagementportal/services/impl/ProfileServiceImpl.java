package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.entities.User;

import com.miu.alumnimanagementportal.dtos.ProfessionalAchievementDto;
import com.miu.alumnimanagementportal.dtos.ProfileDto;
import com.miu.alumnimanagementportal.entities.ProfessionalAchievement;
import com.miu.alumnimanagementportal.entities.Profile;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.ProfileRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class ProfileServiceImpl implements ProfileService {
    private final ProfileRepository repository;
    private final UserRepository userRepository;
    private final Converter converter;
    @Override
    public ProfileDto create(ProfileDto profileDto) {
        Optional.ofNullable(profileDto.getId()).ifPresent(id -> {
            if (repository.existsById(id)) {
                throw new DataAlreadyExistException("Profile with id " + id + " already exists");
            }
        });
        Profile profile = converter.convert(profileDto, Profile.class);
        // Note: User relationship should be set by the caller (UserController)
        Profile saved = repository.save(profile);
        return converter.convert(saved, ProfileDto.class);
    }

    @Override
    public List<ProfileDto> findAll() {
        return converter.convertList(repository.findAll(), ProfileDto.class);
    }

    @Override
    public ProfileDto update(ProfileDto profileDto, Long id) {
        // Try to find profile by the provided id (could be profile id or user id)
        Profile existingProfile = null;
        
        // First, try to find by profile id if provided
        if (profileDto.getId() != null) {
            existingProfile = repository.findById(profileDto.getId()).orElse(null);
        }
        
        // If not found by profile id, try to find by user id
        if (existingProfile == null) {
            User user = userRepository.findById(id).orElse(null);
            if (user != null && user.getProfile() != null) {
                existingProfile = user.getProfile();
            }
        }
        
        if (existingProfile == null) {
            throw new ResourceNotFoundException("Profile with id " + id + " not found");
        }
        
        // Update existing profile with new data
        Profile profileToUpdate = converter.convert(profileDto, Profile.class);
        profileToUpdate.setId(existingProfile.getId());
        profileToUpdate.setUser(existingProfile.getUser()); // Preserve user relationship
        profileToUpdate.setAddress(existingProfile.getAddress()); // Preserve address if exists
        
        Profile saved = repository.save(profileToUpdate);
        return converter.convert(saved, ProfileDto.class);
    }

    @Override
    public ProfileDto getProfileById(Long id) {
        return Optional.ofNullable(id)
                .map(repository::findById)
                .map(profile -> converter.convert(profile, ProfileDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Profile information with id " + id + " not found"));
    }

    @Override
    public ProfileDto getProfileByUserId(Long userId) {
        return userRepository.findById(userId)
                .map(user -> {
                    if (user.getProfile() == null) {
                        throw new ResourceNotFoundException("Profile not found for user with id " + userId);
                    }
                    return converter.convert(user.getProfile(), ProfileDto.class);
                })
                .orElseThrow(() -> new ResourceNotFoundException("User with id " + userId + " not found"));
    }

    @Override
    public void delete(Long id) {
        Optional.ofNullable(id).ifPresent(repository::deleteById);
    }
}
