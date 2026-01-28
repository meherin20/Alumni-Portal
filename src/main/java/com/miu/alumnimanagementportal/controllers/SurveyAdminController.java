package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyCreateRequest;
import com.miu.alumnimanagementportal.surveys.services.SurveyAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin/surveys")
@RequiredArgsConstructor
public class SurveyAdminController {

    private final SurveyAdminService surveyAdminService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> create(@RequestParam String adminEmail,
                                    @Valid @RequestBody SurveyCreateRequest request) {
        return converter.buildResponseEntity(
                Map.of("data", surveyAdminService.create(adminEmail, request)),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@RequestParam String adminEmail,
                                    @PathVariable Long id,
                                    @Valid @RequestBody SurveyCreateRequest request) {
        return converter.buildResponseEntity(
                Map.of("data", surveyAdminService.update(adminEmail, id, request)),
                HttpStatus.OK
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestParam String adminEmail, @PathVariable Long id) {
        surveyAdminService.delete(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Survey deleted"), HttpStatus.OK);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publish(@RequestParam String adminEmail, @PathVariable Long id) {
        surveyAdminService.publish(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Survey published"), HttpStatus.OK);
    }

    @PostMapping("/{id}/unpublish")
    public ResponseEntity<?> unpublish(@RequestParam String adminEmail, @PathVariable Long id) {
        surveyAdminService.unpublish(adminEmail, id);
        return converter.buildResponseEntity(Map.of("message", "Survey unpublished"), HttpStatus.OK);
    }

    @PostMapping("/publish-all-draft")
    public ResponseEntity<?> publishAllDraft(@RequestParam String adminEmail) {
        int count = surveyAdminService.publishAllDraft(adminEmail);
        return converter.buildResponseEntity(
                Map.of("message", "Published " + count + " draft survey(s)", "count", count),
                HttpStatus.OK
        );
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestParam String adminEmail) {
        return converter.buildResponseEntity(
                Map.of("data", surveyAdminService.listAll(adminEmail)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@RequestParam String adminEmail, @PathVariable Long id) {
        return converter.buildResponseEntity(
                Map.of("data", surveyAdminService.getDetail(adminEmail, id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> results(@RequestParam String adminEmail, @PathVariable Long id) {
        return converter.buildResponseEntity(
                Map.of("data", surveyAdminService.getResults(adminEmail, id)),
                HttpStatus.OK
        );
    }
}
