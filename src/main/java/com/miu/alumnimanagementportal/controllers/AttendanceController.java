package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.AttendantDto;
import com.miu.alumnimanagementportal.dtos.EventDto;
import com.miu.alumnimanagementportal.services.AttendantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendantService attendantService;
    private final Converter converter;



    @PostMapping
    public ResponseEntity<?> createAttendance(@Valid @RequestBody AttendantDto attendantDto) {
        attendantService.create(attendantDto);
        return converter.buildResponseEntity(Map.of("message", "Attendant created successfully"), HttpStatus.ACCEPTED);
    }

    @GetMapping
    public ResponseEntity<?> getAttendantAll() {
        return converter.buildResponseEntity(Map.of("data", attendantService.findAll()), HttpStatus.ACCEPTED);
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateAttendantById( @PathVariable Long id,@Valid @RequestBody AttendantDto attendantDto) {
        return converter.buildResponseEntity(Map.of("data", attendantService.update(attendantDto,id)), HttpStatus.OK);
    }






}
