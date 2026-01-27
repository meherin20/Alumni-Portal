package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.FriendRequestCreateDto;
import com.miu.alumnimanagementportal.services.FriendRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/friend-requests")
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody FriendRequestCreateDto dto) {
        return converter.buildResponseEntity(
                Map.of("data", friendRequestService.create(dto)),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/alumni")
    public ResponseEntity<?> getPendingForAlumni(@RequestParam String email) {
        return converter.buildResponseEntity(
                Map.of("data", friendRequestService.getPendingForAlumni(email)),
                HttpStatus.OK
        );
    }

    @GetMapping("/student")
    public ResponseEntity<?> getAcceptedForStudent(@RequestParam String email) {
        return converter.buildResponseEntity(
                Map.of("data", friendRequestService.getAcceptedForStudent(email)),
                HttpStatus.OK
        );
    }

    @GetMapping("/student/pending")
    public ResponseEntity<?> getPendingForStudent(@RequestParam String email) {
        return converter.buildResponseEntity(
                Map.of("data", friendRequestService.getPendingForStudent(email)),
                HttpStatus.OK
        );
    }

    @GetMapping("/alumni/accepted")
    public ResponseEntity<?> getAcceptedForAlumni(@RequestParam String email) {
        return converter.buildResponseEntity(
                Map.of("data", friendRequestService.getAcceptedForAlumni(email)),
                HttpStatus.OK
        );
    }

    @GetMapping("/alumni/all-accepted")
    public ResponseEntity<?> getAllAcceptedConnectionsForAlumni(@RequestParam String email) {
        return converter.buildResponseEntity(
                Map.of("data", friendRequestService.getAllAcceptedConnectionsForAlumni(email)),
                HttpStatus.OK
        );
    }

    @PostMapping("/{id}/accept")
    public ResponseEntity<?> accept(@PathVariable Long id) {
        friendRequestService.accept(id);
        return converter.buildResponseEntity(Map.of("message", "Request accepted"), HttpStatus.OK);
    }

    @PostMapping("/{id}/decline")
    public ResponseEntity<?> decline(@PathVariable Long id) {
        friendRequestService.decline(id);
        return converter.buildResponseEntity(Map.of("message", "Request declined"), HttpStatus.OK);
    }

    @PostMapping("/{id}/block")
    public ResponseEntity<?> block(@PathVariable Long id) {
        friendRequestService.block(id);
        return converter.buildResponseEntity(Map.of("message", "User blocked"), HttpStatus.OK);
    }
}


