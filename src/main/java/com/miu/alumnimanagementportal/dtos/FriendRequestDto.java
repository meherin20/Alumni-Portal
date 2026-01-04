package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.common.enums.FriendRequestStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRequestDto {
    private Long id;
    private String studentName;
    private String studentEmail;
    private String alumniName;
    private String alumniEmail;
    private FriendRequestStatus status;
    private Date createdDate;
}


