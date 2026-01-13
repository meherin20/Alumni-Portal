package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import com.miu.alumnimanagementportal.entities.FriendRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.alumni.email = :alumniEmail AND fr.status = :status")
    List<FriendRequest> findByAlumniEmailAndStatus(@Param("alumniEmail") String alumniEmail, @Param("status") FriendRequestStatus status);

    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END FROM FriendRequest fr WHERE fr.student.email = :studentEmail AND fr.alumni.email = :alumniEmail AND fr.status = :status")
    boolean existsByStudentEmailAndAlumniEmailAndStatus(@Param("studentEmail") String studentEmail, @Param("alumniEmail") String alumniEmail, @Param("status") FriendRequestStatus status);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.student.email = :studentEmail AND fr.status = :status")
    List<FriendRequest> findByStudentEmailAndStatus(@Param("studentEmail") String studentEmail, @Param("status") FriendRequestStatus status);

    @Query("SELECT fr FROM FriendRequest fr WHERE fr.student.email = :studentEmail AND fr.alumni.email = :alumniEmail")
    FriendRequest findByStudentEmailAndAlumniEmail(@Param("studentEmail") String studentEmail, @Param("alumniEmail") String alumniEmail);

    @Query("SELECT CASE WHEN COUNT(fr) > 0 THEN true ELSE false END FROM FriendRequest fr WHERE fr.student.email = :studentEmail AND fr.alumni.email = :alumniEmail")
    boolean existsByStudentEmailAndAlumniEmail(@Param("studentEmail") String studentEmail, @Param("alumniEmail") String alumniEmail);
}
