package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.dtos.FriendRequestCreateDto;
import com.miu.alumnimanagementportal.dtos.FriendRequestDto;
import com.miu.alumnimanagementportal.entities.FriendRequest;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.FriendRequestRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.FriendRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class FriendRequestServiceImpl implements FriendRequestService {

    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;

    @Override
    public FriendRequestDto create(FriendRequestCreateDto request) {
        // Validate student exists and has STUDENT role
        User student = userRepository.findByEmail(request.getStudentEmail());
        if (student == null) {
            throw new ResourceNotFoundException("Student user with email " + request.getStudentEmail() + " not found");
        }
        if (student.getRole() == null || !"STUDENT".equalsIgnoreCase(student.getRole().getTitle())) {
            throw new BadRequestException("User with email " + request.getStudentEmail() + " is not a STUDENT. Only students can send connection requests.");
        }
        
        // Validate alumni exists and has ALUMNI role
        User alumni = userRepository.findByEmail(request.getAlumniEmail());
        if (alumni == null) {
            throw new ResourceNotFoundException("Alumni user with email " + request.getAlumniEmail() + " not found");
        }
        if (alumni.getRole() == null || !"ALUMNI".equalsIgnoreCase(alumni.getRole().getTitle())) {
            throw new BadRequestException("User with email " + request.getAlumniEmail() + " is not an ALUMNI. Connection requests can only be sent to alumni.");
        }
        
        // Prevent self-connection
        if (student.getEmail().equalsIgnoreCase(alumni.getEmail())) {
            throw new BadRequestException("Cannot send connection request to yourself.");
        }

        // Check if a request already exists
        FriendRequest existingRequest = friendRequestRepository.findByStudentEmailAndAlumniEmail(
                request.getStudentEmail(), request.getAlumniEmail());
        
        if (existingRequest != null) {
            FriendRequestStatus existingStatus = existingRequest.getStatus();
            
            if (existingStatus == FriendRequestStatus.PENDING) {
                throw new BadRequestException("A connection request has already been sent to this alumni and is pending approval.");
            } else if (existingStatus == FriendRequestStatus.ACCEPTED) {
                throw new BadRequestException("You are already connected with this alumni.");
            } else if (existingStatus == FriendRequestStatus.BLOCKED) {
                throw new BadRequestException("You cannot send a connection request to this alumni.");
            } else if (existingStatus == FriendRequestStatus.DECLINED) {
                // Allow creating a new request if the previous one was declined
                // Update the existing request to PENDING instead of creating a duplicate
                existingRequest.setStatus(FriendRequestStatus.PENDING);
                FriendRequest saved = friendRequestRepository.save(existingRequest);
                return toDto(saved);
            }
        }

        // Create new request
        FriendRequest fr = new FriendRequest();
        fr.setStudent(student);
        fr.setAlumni(alumni);
        fr.setStatus(FriendRequestStatus.PENDING);
        FriendRequest saved = friendRequestRepository.save(fr);
        return toDto(saved);
    }

    @Override
    public List<FriendRequestDto> getPendingForAlumni(String alumniEmail) {
        // Validate user exists and has ALUMNI role
        User alumni = userRepository.findByEmail(alumniEmail);
        if (alumni == null) {
            throw new ResourceNotFoundException("Alumni user with email " + alumniEmail + " not found");
        }
        if (alumni.getRole() == null || !"ALUMNI".equalsIgnoreCase(alumni.getRole().getTitle())) {
            throw new BadRequestException("User with email " + alumniEmail + " is not an ALUMNI. Only alumni can receive connection requests.");
        }
        
        return friendRequestRepository.findByAlumniEmailAndStatus(alumniEmail, FriendRequestStatus.PENDING)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDto> getAcceptedForStudent(String studentEmail) {
        // Validate user exists and has STUDENT role
        User student = userRepository.findByEmail(studentEmail);
        if (student == null) {
            throw new ResourceNotFoundException("Student user with email " + studentEmail + " not found");
        }
        if (student.getRole() == null || !"STUDENT".equalsIgnoreCase(student.getRole().getTitle())) {
            throw new BadRequestException("User with email " + studentEmail + " is not a STUDENT. Only students can have accepted connections.");
        }
        
        return friendRequestRepository.findByStudentEmailAndStatus(studentEmail, FriendRequestStatus.ACCEPTED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDto> getAcceptedForAlumni(String alumniEmail) {
        // Validate user exists and has ALUMNI role
        User alumni = userRepository.findByEmail(alumniEmail);
        if (alumni == null) {
            throw new ResourceNotFoundException("Alumni user with email " + alumniEmail + " not found");
        }
        if (alumni.getRole() == null || !"ALUMNI".equalsIgnoreCase(alumni.getRole().getTitle())) {
            throw new BadRequestException("User with email " + alumniEmail + " is not an ALUMNI. Only alumni can have accepted connections.");
        }
        
        return friendRequestRepository.findByAlumniEmailAndStatus(alumniEmail, FriendRequestStatus.ACCEPTED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void accept(Long id) {
        FriendRequest fr = friendRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request with id " + id + " not found"));
        fr.setStatus(FriendRequestStatus.ACCEPTED);
        friendRequestRepository.save(fr);
    }

    @Override
    public void decline(Long id) {
        FriendRequest fr = friendRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request with id " + id + " not found"));
        fr.setStatus(FriendRequestStatus.DECLINED);
        friendRequestRepository.save(fr);
    }

    @Override
    public void block(Long id) {
        FriendRequest fr = friendRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Friend request with id " + id + " not found"));
        fr.setStatus(FriendRequestStatus.BLOCKED);
        friendRequestRepository.save(fr);
    }

    private FriendRequestDto toDto(FriendRequest fr) {
        return new FriendRequestDto(
                fr.getId(),
                fr.getStudent().getFirstName() + " " + fr.getStudent().getLastName(),
                fr.getStudent().getEmail(),
                fr.getAlumni().getFirstName() + " " + fr.getAlumni().getLastName(),
                fr.getAlumni().getEmail(),
                fr.getStatus(),
                fr.getCreatedDate()
        );
    }
}


