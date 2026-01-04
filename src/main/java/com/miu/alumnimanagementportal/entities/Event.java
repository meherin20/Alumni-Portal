package com.miu.alumnimanagementportal.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.miu.alumnimanagementportal.common.enums.EventStatus;
import com.miu.alumnimanagementportal.common.enums.EventType;
import com.miu.alumnimanagementportal.common.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

@Data
@Entity
public class Event extends BaseEntity{

    private String title;
    private String description;
    private EventType eventType;
    private String location;
    private Date startDate;
    private LocalDateTime endDate;

    // Enhanced fields for comprehensive event management
    @Column(length = 1000)
    private String shortDescription;

    @Column(length = 2000)
    private String fullDescription;

    @Column(length = 500)
    private String meetingLink;

    @Column(length = 2000)
    private String agenda;

    private Integer maxParticipants;

    @Column(length = 500)
    private String posterUrl;

    @Column(length = 1000)
    private String attachments; // JSON string or comma-separated URLs

    @Column(length = 1000)
    private String speakers;

    @Column(length = 500)
    private String hyperlink;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'UPCOMING'")
    private EventStatus eventStatus;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "VARCHAR(20) DEFAULT 'REGISTER_NOW'")
    private RegistrationStatus registrationStatus;

    @Column(columnDefinition = "BOOLEAN DEFAULT FALSE")
    private boolean featured;

    @ManyToMany
    @JoinTable(name = "Event_users",
            joinColumns = @JoinColumn(name = "event_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id"))
    private Set<User> organizers = new LinkedHashSet<>();

    @OneToMany(mappedBy = "event", orphanRemoval = true)
    private Set<Attendant> attendants = new LinkedHashSet<>();

}
