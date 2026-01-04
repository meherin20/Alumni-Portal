package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobPostDto;
import com.miu.alumnimanagementportal.services.JobPostService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/available-jobs")
public class AvailableJobsController {

    private final Converter converter;
    private final JobPostService jobPostService;

    @GetMapping
    public ResponseEntity<?> getAvailableJobs() {
        List<JobPostDto> publishedJobs = jobPostService.findPublishedJobs();

        List<AvailableJobDto> jobs = publishedJobs.stream()
                .map(job -> new AvailableJobDto(
                        job.getId().toString(),
                        job.getTitle(),
                        job.getDescription(),
                        "Experience not specified", // Could be enhanced with additional fields
                        job.getJobType().toString(),
                        "Salary negotiable", // Could be enhanced with salary field
                        job.getLocation(),
                        // Convert Date to LocalDate, or set a default deadline
                        job.getCreatedDate() != null ?
                            job.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(30) :
                            LocalDate.now().plusDays(30),
                        job.getCompanyName()
                ))
                .collect(Collectors.toList());

        return converter.buildResponseEntity(Map.of("data", jobs), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAvailableJobDetail(@PathVariable String id) {
        try {
            Long jobId = Long.parseLong(id);
            JobPostDto job = jobPostService.getjobPostById(jobId);

            if (!job.isPublished()) {
                return converter.buildResponseEntity(
                        Map.of("message", "Job not found or not published"),
                        HttpStatus.NOT_FOUND
                );
            }

            AvailableJobDetailDto detail = convertToAvailableJobDetailDto(job);
            return converter.buildResponseEntity(Map.of("data", detail), HttpStatus.OK);

        } catch (NumberFormatException e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Invalid job ID format"),
                    HttpStatus.BAD_REQUEST
            );
        } catch (Exception e) {
            return converter.buildResponseEntity(
                    Map.of("message", "Job not found"),
                    HttpStatus.NOT_FOUND
            );
        }
    }

    private AvailableJobDetailDto convertToAvailableJobDetailDto(JobPostDto job) {
        // Calculate days left until some reasonable deadline (30 days from creation)
        LocalDate deadline = job.getCreatedDate() != null ?
            job.getCreatedDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().plusDays(30) :
            LocalDate.now().plusDays(30);

        int daysLeft = (int) java.time.temporal.ChronoUnit.DAYS.between(LocalDate.now(), deadline);

        return new AvailableJobDetailDto(
                job.getId().toString(),
                job.getTitle(),
                job.getLocation(),
                job.getCompanyName(),
                job.getCreatedDate() != null ? job.getCreatedDate().toString() : "Recently posted",
                job.getJobType().toString(),
                "Experience requirements vary", // Could be enhanced with experience field
                deadline.toString(),
                Math.max(0, daysLeft),
                job.getDescription(),
                List.of("Requirements to be specified by employer"), // Could be enhanced with requirements field
                List.of("Duties to be specified by employer"), // Could be enhanced with duties field
                "Please contact the employer directly for application procedures.",
                "Salary and benefits to be discussed with employer.",
                "For more information, please contact " + job.getCompanyName()
        );
    }

    @Data
    @AllArgsConstructor
    private static class AvailableJobDto {
        private String id;
        private String title;
        private String departmentFullName;
        private String experience;
        private String contractType;
        private String salary;
        private String location;
        private LocalDate vacancyUntil;
        private String departmentKey;
    }

    @Data
    @AllArgsConstructor
    private static class AvailableJobDetailDto {
        private String id;
        private String title;
        private String location;
        private String department;
        private String postedOn;
        private String jobType;
        private String experience;
        private String deadline;
        private Integer daysLeft;
        private String introduction;
        private List<String> requirements;
        private List<String> duties;
        private String applicationProcedure;
        private String salaryBenefits;
        private String footerNote;
    }
}


