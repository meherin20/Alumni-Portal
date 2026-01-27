package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.surveys.dtos.SurveySubmitRequest;
import com.miu.alumnimanagementportal.surveys.services.SurveyResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyResponseController {

    private final SurveyResponseService responseService;
    private final Converter converter;

    @PostMapping("/{id}/responses")
    public ResponseEntity<?> submit(@PathVariable Long id, @Valid @RequestBody SurveySubmitRequest request) {
        responseService.submit(id, request);
        return converter.buildResponseEntity(
                Map.of("message", "Survey response submitted"),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/{id}/responses/check")
    public ResponseEntity<?> checkSubmission(@PathVariable Long id, @RequestParam String userEmail) {
        boolean hasSubmitted = responseService.hasUserSubmitted(id, userEmail);
        return converter.buildResponseEntity(
                Map.of("hasSubmitted", hasSubmitted),
                HttpStatus.OK
        );
    }
}
