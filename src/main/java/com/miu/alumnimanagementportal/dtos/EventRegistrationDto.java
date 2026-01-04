package com.miu.alumnimanagementportal.dtos;

import lombok.Data;

import java.io.Serializable;

/**
 * DTO for event registration requests
 */
@Data
public class EventRegistrationDto implements Serializable {
    private Long eventId;
    private Long userId;
    private String userName;
    private String userEmail;
    private String registrationNotes;
}
