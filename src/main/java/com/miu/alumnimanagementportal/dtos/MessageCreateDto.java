package com.miu.alumnimanagementportal.dtos;

import lombok.Data;

@Data
public class MessageCreateDto {
    private String senderEmail;
    private String recipientEmail;
    private String content;
}


