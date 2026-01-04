package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobApplicationDto;
import com.miu.alumnimanagementportal.services.JobApplicationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/job-applications")
public class JobApplicationController {

    private final JobApplicationService jobApplicationService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> createJobApplication(
            @Valid @ModelAttribute JobApplicationDto jobApplicationDto,
            @RequestParam(value = "photoFile", required = false) MultipartFile photoFile,
            @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile) {
        try {
            JobApplicationDto createdApplication = jobApplicationService.create(jobApplicationDto, photoFile, resumeFile);
            return converter.buildResponseEntity(
                    Map.of("message", "Job application submitted successfully", "data", createdApplication),
                    HttpStatus.CREATED);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to submit application: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllJobApplications() {
        return converter.buildResponseEntity(
                Map.of("data", jobApplicationService.findAll()),
                HttpStatus.OK);
    }

    @GetMapping("/job/{jobPostId}")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable Long jobPostId) {
        return converter.buildResponseEntity(
                Map.of("data", jobApplicationService.findByJobPostId(jobPostId)),
                HttpStatus.OK);
    }

    @GetMapping("/applicant/{applicantId}")
    public ResponseEntity<?> getApplicationsByApplicant(@PathVariable Long applicantId) {
        return converter.buildResponseEntity(
                Map.of("data", jobApplicationService.findByApplicantId(applicantId)),
                HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobApplicationById(@PathVariable Long id) {
        return converter.buildResponseEntity(
                Map.of("data", jobApplicationService.getJobApplicationById(id)),
                HttpStatus.OK);
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateApplicationStatus(@PathVariable Long id, @RequestParam String status) {
        try {
            JobApplicationDto updatedApplication = jobApplicationService.updateStatus(id, status);
            return converter.buildResponseEntity(
                    Map.of("message", "Application status updated successfully", "data", updatedApplication),
                    HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to update status: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobApplication(@PathVariable Long id) {
        try {
            jobApplicationService.delete(id);
            return converter.buildResponseEntity(
                    Map.of("message", "Job application deleted successfully"),
                    HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Failed to delete application: " + e.getMessage()),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/check/{jobPostId}/{applicantId}")
    public ResponseEntity<?> checkApplication(@PathVariable Long jobPostId, @PathVariable Long applicantId) {
        boolean hasApplied = jobApplicationService.hasUserAppliedForJob(jobPostId, applicantId);
        return converter.buildResponseEntity(
                Map.of("hasApplied", hasApplied),
                HttpStatus.OK);
    }
}
