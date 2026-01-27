package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.AlumniDirectoryDto;
import com.miu.alumnimanagementportal.services.AlumniDirectoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/alumni-directory")
public class AlumniDirectoryController {

    private final AlumniDirectoryService alumniDirectoryService;
    private final Converter converter;

    @GetMapping("/search")
    public ResponseEntity<?> searchAlumni(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String graduationYear,
            @RequestParam(required = false) String jobTitle,
            @RequestParam(required = false) String company) {
        try {
            List<AlumniDirectoryDto> results = alumniDirectoryService.searchAlumni(name, email, department, graduationYear, jobTitle, company);
            return converter.buildResponseEntity(
                    Map.of("data", results),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            e.printStackTrace();
            return converter.buildResponseEntity(
                    Map.of("success", false, "message", "Error searching alumni: " + e.getMessage(), "data", List.of()),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAlumniProfile(@PathVariable Long id) {
        return converter.buildResponseEntity(
                Map.of("data", alumniDirectoryService.getAlumniProfile(id)),
                HttpStatus.OK
        );
    }

    @GetMapping("/check-connection")
    public ResponseEntity<?> checkConnection(
            @RequestParam String studentEmail,
            @RequestParam String alumniEmail) {
        return converter.buildResponseEntity(
                Map.of("data", alumniDirectoryService.checkConnectionStatus(studentEmail, alumniEmail)),
                HttpStatus.OK
        );
    }
}

