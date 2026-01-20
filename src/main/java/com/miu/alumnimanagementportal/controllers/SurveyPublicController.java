package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.surveys.services.SurveyPublicService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/surveys")
@RequiredArgsConstructor
public class SurveyPublicController {

    private final SurveyPublicService surveyPublicService;
    private final Converter converter;

    @GetMapping
    public ResponseEntity<?> list() {
        return converter.buildResponseEntity(
                Map.of("data", surveyPublicService.listPublished()),
                HttpStatus.OK
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> detail(@PathVariable Long id) {
        return converter.buildResponseEntity(
                Map.of("data", surveyPublicService.getPublishedSurvey(id)),
                HttpStatus.OK
        );
    }
}
