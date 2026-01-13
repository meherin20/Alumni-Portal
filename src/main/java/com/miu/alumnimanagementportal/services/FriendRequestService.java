package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.FriendRequestCreateDto;
import com.miu.alumnimanagementportal.dtos.FriendRequestDto;

import java.util.List;

public interface FriendRequestService {

    FriendRequestDto create(FriendRequestCreateDto request);

    List<FriendRequestDto> getPendingForAlumni(String alumniEmail);

    List<FriendRequestDto> getAcceptedForStudent(String studentEmail);

    List<FriendRequestDto> getAcceptedForAlumni(String alumniEmail);

    void accept(Long id);

    void decline(Long id);

    void block(Long id);
}


