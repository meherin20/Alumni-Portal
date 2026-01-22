package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobApplicationDto;
import com.miu.alumnimanagementportal.entities.JobApplication;
import com.miu.alumnimanagementportal.entities.JobPost;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.JobApplicationRepository;
import com.miu.alumnimanagementportal.repositories.JobPostRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.JobApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository jobApplicationRepository;
    private final JobPostRepository jobPostRepository;
    private final UserRepository userRepository;
    private final Converter converter;

    private static final String UPLOAD_DIR = "uploads/job-applications/";

    @Override
    public JobApplicationDto create(JobApplicationDto jobApplicationDto, MultipartFile photoFile, MultipartFile resumeFile) {
        // Validate that only students and alumni can submit applications
        if (jobApplicationDto.getEmail() != null && !jobApplicationDto.getEmail().isBlank()) {
            User applicantUser = userRepository.findByEmail(jobApplicationDto.getEmail());
            if (applicantUser != null && applicantUser.getRole() != null) {
                String userRole = applicantUser.getRole().getTitle().toUpperCase().trim();
                if (!userRole.equals("STUDENT") && !userRole.equals("ALUMNI")) {
                    throw new BadRequestException("Only students and alumni can submit job applications. Your account is registered as " + userRole + ".");
                }
            }
        }

        // If applicantId is provided, also validate the role
        if (jobApplicationDto.getApplicantId() != null) {
            User applicant = userRepository.findById(jobApplicationDto.getApplicantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
            if (applicant.getRole() != null) {
                String userRole = applicant.getRole().getTitle().toUpperCase().trim();
                if (!userRole.equals("STUDENT") && !userRole.equals("ALUMNI")) {
                    throw new BadRequestException("Only students and alumni can submit job applications. Your account is registered as " + userRole + ".");
                }
            }
        }

        // Check if user has already applied for this job
        if (jobApplicationDto.getApplicantId() != null &&
            hasUserAppliedForJob(jobApplicationDto.getJobPostId(), jobApplicationDto.getApplicantId())) {
            throw new DataAlreadyExistException("You have already applied for this job");
        }

        JobApplication jobApplication = new JobApplication();

        // Set job post
        JobPost jobPost = jobPostRepository.findById(jobApplicationDto.getJobPostId())
                .orElseThrow(() -> new ResourceNotFoundException("Job post not found"));
        jobApplication.setJobPost(jobPost);

        // Set applicant (optional)
        if (jobApplicationDto.getApplicantId() != null) {
            User applicant = userRepository.findById(jobApplicationDto.getApplicantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Applicant not found"));
            jobApplication.setApplicant(applicant);
        }

        // Set basic info
        jobApplication.setFullName(jobApplicationDto.getFullName());
        jobApplication.setEmail(jobApplicationDto.getEmail());
        jobApplication.setPhoneNumber(jobApplicationDto.getPhoneNumber());
        jobApplication.setCurrentWorkplace(jobApplicationDto.getCurrentWorkplace());
        jobApplication.setPortfolioLink(jobApplicationDto.getPortfolioLink());
        jobApplication.setGithubLink(jobApplicationDto.getGithubLink());
        jobApplication.setExperience(jobApplicationDto.getExperience());
        jobApplication.setCoverLetter(jobApplicationDto.getCoverLetter());
        jobApplication.setSkills(jobApplicationDto.getSkills());

        // Handle file uploads
        if (photoFile != null && !photoFile.isEmpty()) {
            String photoFileName = saveFile(photoFile, "photo");
            jobApplication.setPhotoFileName(photoFileName);
        }

        if (resumeFile != null && !resumeFile.isEmpty()) {
            String resumeFileName = saveFile(resumeFile, "resume");
            jobApplication.setResumeFileName(resumeFileName);
        }

        JobApplication savedApplication = jobApplicationRepository.save(jobApplication);
        return convertToDto(savedApplication);
    }

    @Override
    public List<JobApplicationDto> findAll() {
        return jobApplicationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDto> findByJobPostId(Long jobPostId) {
        return jobApplicationRepository.findByJobPostId(jobPostId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobApplicationDto> findByApplicantId(Long applicantId) {
        return jobApplicationRepository.findByApplicantId(applicantId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobApplicationDto getJobApplicationById(Long id) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));
        return convertToDto(application);
    }

    @Override
    public JobApplicationDto updateStatus(Long id, String status) {
        JobApplication application = jobApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job application not found"));

        try {
            JobApplication.ApplicationStatus applicationStatus = JobApplication.ApplicationStatus.valueOf(status.toUpperCase());
            application.setStatus(applicationStatus);
            JobApplication savedApplication = jobApplicationRepository.save(application);
            return convertToDto(savedApplication);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: " + status);
        }
    }

    @Override
    public void delete(Long id) {
        if (!jobApplicationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Job application not found");
        }
        jobApplicationRepository.deleteById(id);
    }

    @Override
    public boolean hasUserAppliedForJob(Long jobPostId, Long applicantId) {
        return jobApplicationRepository.existsByJobPostIdAndApplicantId(jobPostId, applicantId);
    }

    private String saveFile(MultipartFile file, String type) {
        try {
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null && originalFilename.contains(".")
                    ? originalFilename.substring(originalFilename.lastIndexOf("."))
                    : "";
            String filename = type + "_" + UUID.randomUUID().toString() + extension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save " + type + " file", e);
        }
    }

    private JobApplicationDto convertToDto(JobApplication application) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(application.getId());
        dto.setVersion(application.getVersion());
        dto.setCreatedDate(application.getCreatedDate());
        dto.setLastModifiedDate(application.getLastModifiedDate());

        if (application.getJobPost() != null) {
            dto.setJobPostId(application.getJobPost().getId());
            dto.setJobTitle(application.getJobPost().getTitle());
            dto.setJobCompany(application.getJobPost().getCompanyName());
        }

        if (application.getApplicant() != null) {
            dto.setApplicantId(application.getApplicant().getId());
            dto.setApplicantName(application.getApplicant().getFirstName() + " " + application.getApplicant().getLastName());
        }

        dto.setFullName(application.getFullName());
        dto.setEmail(application.getEmail());
        dto.setPhoneNumber(application.getPhoneNumber());
        dto.setCurrentWorkplace(application.getCurrentWorkplace());
        dto.setPortfolioLink(application.getPortfolioLink());
        dto.setGithubLink(application.getGithubLink());
        dto.setExperience(application.getExperience());
        dto.setCoverLetter(application.getCoverLetter());
        dto.setSkills(application.getSkills());
        dto.setResumeFileName(application.getResumeFileName());
        dto.setPhotoFileName(application.getPhotoFileName());
        dto.setStatus(application.getStatus().toString());

        return dto;
    }
}
