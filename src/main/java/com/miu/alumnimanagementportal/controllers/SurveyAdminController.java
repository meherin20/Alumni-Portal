package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.SurveyCreateUpdateDto;
import com.miu.alumnimanagementportal.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.dtos.SurveyResultsDto;
import com.miu.alumnimanagementportal.services.SurveyAdminService;
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

    private final SurveyAdminService adminService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody SurveyCreateUpdateDto dto) {
        SurveyDetailDto created = adminService.create(dto);
        return converter.buildResponseEntity(Map.of("data", created), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @Valid @RequestBody SurveyCreateUpdateDto dto) {
        SurveyDetailDto updated = adminService.update(id, dto);
        return converter.buildResponseEntity(Map.of("data", updated), HttpStatus.OK);
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<?> publish(@PathVariable Long id) {
        adminService.publish(id);
        return converter.buildResponseEntity(Map.of("message", "Survey published"), HttpStatus.OK);
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<?> close(@PathVariable Long id) {
        adminService.close(id);
        return converter.buildResponseEntity(Map.of("message", "Survey closed"), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable Long id) {
        SurveyDetailDto dto = adminService.get(id);
        return converter.buildResponseEntity(Map.of("data", dto), HttpStatus.OK);
    }

    @GetMapping("/{id}/results")
    public ResponseEntity<?> results(@PathVariable Long id) {
        SurveyResultsDto results = adminService.getResults(id);
        return converter.buildResponseEntity(Map.of("data", results), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        adminService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "Survey deleted"), HttpStatus.OK);
    }

    @GetMapping("/non-anonymous")
    public ResponseEntity<?> listNonAnonymous() {
        return converter.buildResponseEntity(Map.of("data", adminService.listNonAnonymous()), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<?> listAll() {
        return converter.buildResponseEntity(Map.of("data", adminService.listAll()), HttpStatus.OK);
    }
}


