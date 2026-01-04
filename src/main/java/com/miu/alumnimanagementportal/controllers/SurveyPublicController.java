package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.dtos.SurveyResponseCreateDto;
import com.miu.alumnimanagementportal.dtos.SurveySummaryDto;
import com.miu.alumnimanagementportal.services.SurveyResponseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/surveys-module")
@RequiredArgsConstructor
public class SurveyPublicController {

    private final SurveyResponseService responseService;
    private final Converter converter;

    @GetMapping
    public ResponseEntity<?> listOpen(
            @RequestParam(required = false) String dept,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) String country
    ) {
        List<SurveySummaryDto> surveys = responseService.listOpen(dept, year, country);
        return converter.buildResponseEntity(Map.of("data", surveys), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSurvey(@PathVariable Long id) {
        SurveyDetailDto dto = responseService.getSurvey(id);
        return converter.buildResponseEntity(Map.of("data", dto), HttpStatus.OK);
    }

    @PostMapping("/{id}/responses")
    public ResponseEntity<?> submit(
            @PathVariable Long id,
            @RequestParam(defaultValue = "false") boolean anonymous,
            @Valid @RequestBody SurveyResponseCreateDto dto
    ) {
        responseService.submitResponse(id, dto, anonymous);
        return converter.buildResponseEntity(Map.of("message", "Response submitted"), HttpStatus.CREATED);
    }
}


