package com.miu.alumnimanagementportal.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miu.alumnimanagementportal.common.enums.EventStatus;
import com.miu.alumnimanagementportal.common.enums.EventType;
import com.miu.alumnimanagementportal.common.enums.RegistrationStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * DTO for {@link com.miu.alumnimanagementportal.entities.Event}
 */
@Data
public class EventDto implements Serializable {
    Long id;
    Long version;
    Date createdDate;
    Date lastModifiedDate;

    @NotNull
    @NotEmpty
    @NotBlank
    String title;

    String description;
    String shortDescription;
    String fullDescription;

    @NotNull
    EventType eventType;

    @NotNull
    @NotEmpty
    @NotBlank
    String location;

    Date startDate;

    LocalDateTime endDate;

    // Enhanced fields
    String meetingLink;
    String agenda;
    Integer maxParticipants;
    String posterUrl;
    String attachments;
    String speakers;
    String hyperlink;

    private EventStatus eventStatus = EventStatus.UPCOMING;
    private RegistrationStatus registrationStatus = RegistrationStatus.REGISTER_NOW;

    boolean featured = false;
}