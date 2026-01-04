package com.miu.alumnimanagementportal.dtos;

import lombok.Data;

@Data
public class FriendRequestCreateDto {
    private String studentEmail;
    private String alumniEmail;
}


