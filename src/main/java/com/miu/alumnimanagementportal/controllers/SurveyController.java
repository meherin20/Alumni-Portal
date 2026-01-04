package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.JobPostDto;
import com.miu.alumnimanagementportal.dtos.ProfileDto;
import com.miu.alumnimanagementportal.dtos.SurveyDto;
import com.miu.alumnimanagementportal.dtos.UserDto;
import com.miu.alumnimanagementportal.services.ProfileService;
import com.miu.alumnimanagementportal.services.SurveyResponseService;
import com.miu.alumnimanagementportal.services.SurveyService;
import com.miu.alumnimanagementportal.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/surveys")
public class SurveyController {
    private final SurveyService surveyService;
    private final Converter converter;
    private final SurveyResponseService surveyResponseService;

    @PostMapping
    public ResponseEntity<?> createSurvey(@Valid @RequestBody SurveyDto surveyDto) {
        surveyService.create(surveyDto);
        return converter.buildResponseEntity(Map.of("message", "Survey created successfully"), HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<?> getSurveyAll() {
        return converter.buildResponseEntity(Map.of("data", surveyService.findAll()), HttpStatus.ACCEPTED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getSurveyById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("data", surveyService.getSurveyById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSurveyById( @PathVariable Long id,@Valid @RequestBody SurveyDto surveyDto) {
        return converter.buildResponseEntity(Map.of("data", surveyService.update(surveyDto, id)), HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSurveyById(@PathVariable Long id) {
        surveyService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "Survey Deleted successfully"), HttpStatus.ACCEPTED);
    }

    @GetMapping("/active/count")
    public ResponseEntity<?> getActiveSurveyCount() {
        long submitted = surveyResponseService.countSubmitted();
        return converter.buildResponseEntity(Map.of("data", submitted), HttpStatus.OK);
    }


}
