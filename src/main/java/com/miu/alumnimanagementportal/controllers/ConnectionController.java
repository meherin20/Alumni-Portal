package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.FriendRequestCreateDto;
import com.miu.alumnimanagementportal.entities.FriendRequest;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.FriendRequestRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/connections")
public class ConnectionController {

    private final UserRepository userRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final Converter converter;

    @PostMapping("/alumni-to-alumni")
    public ResponseEntity<?> createAlumniToAlumniConnection(@RequestBody Map<String, String> request) {
        String requesterEmail = request.get("requesterEmail");
        String targetEmail = request.get("targetEmail");

        if (requesterEmail == null || targetEmail == null) {
            throw new BadRequestException("Requester email and target email are required");
        }

        User requester = userRepository.findByEmail(requesterEmail);
        if (requester == null || requester.getRole() == null || !"ALUMNI".equalsIgnoreCase(requester.getRole().getTitle())) {
            throw new BadRequestException("Requester must be an ALUMNI");
        }

        User target = userRepository.findByEmail(targetEmail);
        if (target == null || target.getRole() == null || !"ALUMNI".equalsIgnoreCase(target.getRole().getTitle())) {
            throw new BadRequestException("Target must be an ALUMNI");
        }

        if (requesterEmail.equalsIgnoreCase(targetEmail)) {
            throw new BadRequestException("Cannot send connection request to yourself");
        }

        // Check if connection already exists (bidirectional check)
        FriendRequest existing = friendRequestRepository.findByStudentEmailAndAlumniEmail(requesterEmail, targetEmail);
        if (existing == null) {
            existing = friendRequestRepository.findByStudentEmailAndAlumniEmail(targetEmail, requesterEmail);
        }

        if (existing != null) {
            FriendRequestStatus status = existing.getStatus();
            if (status == FriendRequestStatus.PENDING) {
                throw new BadRequestException("Connection request already pending");
            } else if (status == FriendRequestStatus.ACCEPTED) {
                throw new BadRequestException("Already connected");
            } else if (status == FriendRequestStatus.BLOCKED) {
                throw new BadRequestException("Cannot send connection request");
            } else if (status == FriendRequestStatus.DECLINED) {
                existing.setStatus(FriendRequestStatus.PENDING);
                friendRequestRepository.save(existing);
                return converter.buildResponseEntity(Map.of("message", "Connection request sent"), HttpStatus.CREATED);
            }
        }

        // Create new connection (using student-alumni structure, treating requester as student)
        FriendRequest fr = new FriendRequest();
        fr.setStudent(requester);
        fr.setAlumni(target);
        fr.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(fr);

        return converter.buildResponseEntity(Map.of("message", "Connection request sent"), HttpStatus.CREATED);
    }

    @PostMapping("/alumni-to-student")
    public ResponseEntity<?> createAlumniToStudentConnection(@RequestBody Map<String, String> request) {
        String alumniEmail = request.get("alumniEmail");
        String studentEmail = request.get("studentEmail");

        if (alumniEmail == null || studentEmail == null) {
            throw new BadRequestException("Alumni email and student email are required");
        }

        User alumni = userRepository.findByEmail(alumniEmail);
        if (alumni == null || alumni.getRole() == null || !"ALUMNI".equalsIgnoreCase(alumni.getRole().getTitle())) {
            throw new BadRequestException("Requester must be an ALUMNI");
        }

        User student = userRepository.findByEmail(studentEmail);
        if (student == null || student.getRole() == null || !"STUDENT".equalsIgnoreCase(student.getRole().getTitle())) {
            throw new BadRequestException("Target must be a STUDENT");
        }

        if (alumniEmail.equalsIgnoreCase(studentEmail)) {
            throw new BadRequestException("Cannot send connection request to yourself");
        }

        // Check if connection already exists
        FriendRequest existing = friendRequestRepository.findByStudentEmailAndAlumniEmail(studentEmail, alumniEmail);

        if (existing != null) {
            FriendRequestStatus status = existing.getStatus();
            if (status == FriendRequestStatus.PENDING) {
                throw new BadRequestException("Connection request already pending");
            } else if (status == FriendRequestStatus.ACCEPTED) {
                throw new BadRequestException("Already connected");
            } else if (status == FriendRequestStatus.BLOCKED) {
                throw new BadRequestException("Cannot send connection request");
            } else if (status == FriendRequestStatus.DECLINED) {
                existing.setStatus(FriendRequestStatus.PENDING);
                friendRequestRepository.save(existing);
                return converter.buildResponseEntity(Map.of("message", "Connection request sent"), HttpStatus.CREATED);
            }
        }

        // Create new connection (alumni can initiate, but we store it as student-to-alumni)
        FriendRequest fr = new FriendRequest();
        fr.setStudent(student);
        fr.setAlumni(alumni);
        fr.setStatus(FriendRequestStatus.PENDING);
        friendRequestRepository.save(fr);

        return converter.buildResponseEntity(Map.of("message", "Connection request sent"), HttpStatus.CREATED);
    }
}
