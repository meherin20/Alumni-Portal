package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.dtos.FriendRequestCreateDto;
import com.miu.alumnimanagementportal.dtos.FriendRequestDto;
import com.miu.alumnimanagementportal.entities.FriendRequest;
import com.miu.alumnimanagementportal.entities.User;
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
        User student = userRepository.findByEmail(request.getStudentEmail());
        if (student == null) {
            throw new ResourceNotFoundException("Student user with email " + request.getStudentEmail() + " not found");
        }
        User alumni = userRepository.findByEmail(request.getAlumniEmail());
        if (alumni == null) {
            throw new ResourceNotFoundException("Alumni user with email " + request.getAlumniEmail() + " not found");
        }

        FriendRequest fr = new FriendRequest();
        fr.setStudent(student);
        fr.setAlumni(alumni);
        fr.setStatus(FriendRequestStatus.PENDING);
        FriendRequest saved = friendRequestRepository.save(fr);
        return toDto(saved);
    }

    @Override
    public List<FriendRequestDto> getPendingForAlumni(String alumniEmail) {
        return friendRequestRepository.findByAlumniEmailAndStatus(alumniEmail, FriendRequestStatus.PENDING)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDto> getAcceptedForStudent(String studentEmail) {
        return friendRequestRepository.findByStudentEmailAndStatus(studentEmail, FriendRequestStatus.ACCEPTED)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequestDto> getAcceptedForAlumni(String alumniEmail) {
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


