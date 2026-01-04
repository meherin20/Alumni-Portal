package com.miu.alumnimanagementportal.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MessageDto {
    private Long id;
    private String senderName;
    private String senderEmail;
    private String recipientEmail;
    private String content;
    private Date createdDate;
}


