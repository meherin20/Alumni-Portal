package com.miu.alumnimanagementportal.entities;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
@Entity
public class FriendRequest extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "student_id")
    private User student;

    @ManyToOne
    @JoinColumn(name = "alumni_id")
    private User alumni;

    @Enumerated(EnumType.STRING)
    private FriendRequestStatus status = FriendRequestStatus.PENDING;
}


