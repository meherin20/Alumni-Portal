package com.miu.alumnimanagementportal.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String content;
    private String category;
    private String source;
    private String userEmail;
    private String reply;
    private LocalDateTime timestamp;
    private LocalDateTime repliedAt;
    private Boolean isRead;
}
