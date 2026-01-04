package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobPostDto;
import com.miu.alumnimanagementportal.services.JobPostService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin/jobs")
public class JobAdminController {
    private final JobPostService jobPostService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> createJobPost(@Valid @RequestBody JobPostDto jobPostDto) {
        try {
            jobPostService.create(jobPostDto);
            return converter.buildResponseEntity(Map.of("message", "Job post created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("message", "Failed to create job post: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllJobPosts() {
        return converter.buildResponseEntity(Map.of("success", true, "data", jobPostService.findAll()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getJobPostById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("success", true, "data", jobPostService.getjobPostById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateJobPost(@PathVariable Long id, @Valid @RequestBody JobPostDto jobPostDto) {
        return converter.buildResponseEntity(Map.of("success", true, "data", jobPostService.update(jobPostDto, id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJobPost(@PathVariable Long id) {
        jobPostService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "Job post deleted successfully"), HttpStatus.OK);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<?> publishJobPost(@PathVariable Long id) {
        try {
            jobPostService.publishJob(id);
            return converter.buildResponseEntity(Map.of("message", "Job post published successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("message", "Failed to publish job post: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublishJobPost(@PathVariable Long id) {
        try {
            jobPostService.unpublishJob(id);
            return converter.buildResponseEntity(Map.of("message", "Job post unpublished successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("message", "Failed to unpublish job post: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/publish-all")
    public ResponseEntity<?> publishAllJobPosts() {
        try {
            jobPostService.publishAllJobs();
            return converter.buildResponseEntity(Map.of("message", "All job posts published successfully"), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of("message", "Failed to publish all job posts: " + e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
