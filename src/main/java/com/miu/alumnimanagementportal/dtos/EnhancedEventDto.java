package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.common.enums.EventStatus;
import com.miu.alumnimanagementportal.common.enums.EventType;
import com.miu.alumnimanagementportal.common.enums.RegistrationStatus;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * Enhanced DTO for comprehensive event information
 */
@Data
public class EnhancedEventDto implements Serializable {
    private Long id;
    private Long version;
    private Date createdDate;
    private Date lastModifiedDate;

    // Basic event information
    private String title;
    private String shortDescription;
    private String fullDescription;
    private EventType eventType;
    private String location;
    private Date startDate;
    private LocalDateTime endDate;

    // Enhanced event details
    private String meetingLink;
    private String agenda;
    private Integer maxParticipants;
    private Integer currentParticipants;
    private String posterUrl;
    private String attachments;
    private String speakers; // JSON string with speaker details
    private String hyperlink;

    // Status information
    private EventStatus eventStatus;
    private RegistrationStatus registrationStatus;
    private boolean featured;

    // Related data
    private List<String> organizerNames;
    private boolean isUserRegistered;
    private Integer seatsAvailable;

    // Computed fields
    private String eventTypeLabel;
    private String eventStatusLabel;
    private String registrationStatusLabel;
}
