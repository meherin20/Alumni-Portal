package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.entities.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    List<FriendRequest> findByAlumniEmailAndStatus(String alumniEmail, FriendRequestStatus status);

    boolean existsByStudentEmailAndAlumniEmailAndStatus(String studentEmail, String alumniEmail, FriendRequestStatus status);

    List<FriendRequest> findByStudentEmailAndStatus(String studentEmail, FriendRequestStatus status);
}


