package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.dtos.*;
import com.miu.alumnimanagementportal.entities.FriendRequest;
import com.miu.alumnimanagementportal.entities.Profile;
import com.miu.alumnimanagementportal.entities.Role;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.FriendRequestRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.AlumniDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class AlumniDirectoryServiceImpl implements AlumniDirectoryService {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final Converter converter;

    @Override
    @Transactional(readOnly = true)
    public List<AlumniDirectoryDto> searchAlumni(String name, String department, String graduationYear, String jobTitle, String company) {
        // Get all users with ALUMNI role
        List<User> allUsers = userRepository.findAll();
        
        List<User> alumniUsers = allUsers.stream()
                .filter(user -> {
                    // ONE user = ONE role - check if role is ALUMNI
                    if (user.getRole() == null) return false;
                    return "ALUMNI".equalsIgnoreCase(user.getRole().getTitle());
                })
                .filter(user -> {
                    // Filter by name (works even without profile)
                    if (name != null && !name.trim().isEmpty()) {
                        String fullName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                        if (!fullName.contains(name.toLowerCase().trim())) {
                            return false;
                        }
                    }
                    
                    // Filter by department, graduation year, job title, company from profile
                    Profile profile = user.getProfile();
                    
                    // If profile-based filters are specified, user must have a profile
                    boolean hasProfileFilters = (department != null && !department.trim().isEmpty()) ||
                                               (graduationYear != null && !graduationYear.trim().isEmpty()) ||
                                               (jobTitle != null && !jobTitle.trim().isEmpty()) ||
                                               (company != null && !company.trim().isEmpty());
                    
                    if (hasProfileFilters && profile == null) {
                        // If filters require profile but user doesn't have one, exclude them
                        return false;
                    }
                    
                    if (profile != null) {
                        if (department != null && !department.trim().isEmpty()) {
                            if (profile.getDepartment() == null || 
                                !profile.getDepartment().toLowerCase().contains(department.toLowerCase().trim())) {
                                return false;
                            }
                        }
                        
                        if (graduationYear != null && !graduationYear.trim().isEmpty()) {
                            if (profile.getPassingYear() == null || 
                                !profile.getPassingYear().equals(graduationYear.trim())) {
                                return false;
                            }
                        }
                        
                        if (jobTitle != null && !jobTitle.trim().isEmpty()) {
                            if (profile.getJobTitle() == null || 
                                !profile.getJobTitle().toLowerCase().contains(jobTitle.toLowerCase().trim())) {
                                return false;
                            }
                        }
                        
                        if (company != null && !company.trim().isEmpty()) {
                            if (profile.getWorkExperiences() == null || profile.getWorkExperiences().isEmpty()) {
                                return false;
                            }
                            boolean found = profile.getWorkExperiences().stream()
                                    .anyMatch(we -> we.getCompanyName() != null && 
                                            we.getCompanyName().toLowerCase().contains(company.toLowerCase().trim()));
                            if (!found) {
                                return false;
                            }
                        }
                    }
                    
                    return true;
                })
                .collect(Collectors.toList());
        
        return alumniUsers.stream()
                .map(this::toAlumniDirectoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AlumniDirectoryDto getAlumniProfile(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alumni with id " + id + " not found"));
        
        // Verify user is an alumni - ONE user = ONE role
        boolean isAlumni = user.getRole() != null && "ALUMNI".equalsIgnoreCase(user.getRole().getTitle());
        
        if (!isAlumni) {
            throw new ResourceNotFoundException("User with id " + id + " is not an alumni");
        }
        
        return toAlumniDirectoryDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public ConnectionStatusDto checkConnectionStatus(String studentEmail, String alumniEmail) {
        FriendRequest request = friendRequestRepository.findByStudentEmailAndAlumniEmail(studentEmail, alumniEmail);
        
        ConnectionStatusDto dto = new ConnectionStatusDto();
        if (request == null) {
            dto.setConnected(false);
            dto.setCanMessage(false);
            dto.setStatus(null);
            dto.setRequestId(null);
        } else {
            dto.setConnected(request.getStatus() == FriendRequestStatus.ACCEPTED);
            dto.setCanMessage(request.getStatus() == FriendRequestStatus.ACCEPTED);
            dto.setStatus(request.getStatus());
            dto.setRequestId(request.getId());
        }
        
        return dto;
    }

    private AlumniDirectoryDto toAlumniDirectoryDto(User user) {
        AlumniDirectoryDto dto = new AlumniDirectoryDto();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        
        Profile profile = user.getProfile();
        if (profile != null) {
            dto.setProfileImage(profile.getProfileImage());
            dto.setDepartment(profile.getDepartment());
            dto.setPassingYear(profile.getPassingYear());
            dto.setJobTitle(profile.getJobTitle());
            dto.setPhone(profile.getPhone());
            dto.setCvLink(profile.getCvLink());
            dto.setPortfolioLink(profile.getPortfolioLink());
            
            // Get company from latest work experience
            if (profile.getWorkExperiences() != null && !profile.getWorkExperiences().isEmpty()) {
                String latestCompany = profile.getWorkExperiences().stream()
                        .filter(we -> we.getCompanyName() != null)
                        .findFirst()
                        .map(we -> we.getCompanyName())
                        .orElse(null);
                dto.setCompany(latestCompany);
            }
            
            // Convert address
            if (profile.getAddress() != null) {
                dto.setAddress(converter.convert(profile.getAddress(), AddressDto.class));
            }
            
            // Convert work experiences
            if (profile.getWorkExperiences() != null) {
                dto.setWorkExperiences(profile.getWorkExperiences().stream()
                        .map(we -> converter.convert(we, WorkExperienceDto.class))
                        .collect(Collectors.toList()));
            }
            
            // Convert professional achievements
            if (profile.getProfessionalAchievements() != null) {
                dto.setProfessionalAchievements(profile.getProfessionalAchievements().stream()
                        .map(pa -> converter.convert(pa, ProfessionalAchievementDto.class))
                        .collect(Collectors.toList()));
            }
            
            // Convert education details
            if (profile.getEducationDetails() != null) {
                dto.setEducationDetails(profile.getEducationDetails().stream()
                        .map(ed -> converter.convert(ed, EducationDetailsDto.class))
                        .collect(Collectors.toList()));
            }
        }
        
        return dto;
    }
}

