package com.miu.alumnimanagementportal.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String content;

    @Column(length = 100)
    private String category; // e.g., "survey-feedback", "general"

    @Column(length = 100)
    private String source; // e.g., "survey-page", "contact-form"

    @Column(length = 255)
    private String userEmail; // Optional user identifier

    @Column(length = 500)
    private String reply; // Admin reply

    @Column
    private LocalDateTime timestamp;

    @Column
    private LocalDateTime repliedAt;

    @Column
    private Boolean isRead = false;

    @PrePersist
    protected void onCreate() {
        timestamp = LocalDateTime.now();
    }
}
