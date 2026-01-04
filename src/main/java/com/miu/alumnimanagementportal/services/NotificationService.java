package com.miu.alumnimanagementportal.services;

import java.util.List;

public interface NotificationService {

    /**
     * Send email notification to a single recipient
     */
    void sendEmail(String to, String subject, String message);

    /**
     * Send email notification to multiple recipients
     */
    void sendEmail(List<String> toList, String subject, String message);

    /**
     * Send event-specific notification to all registered attendees
     */
    void sendEventNotification(Long eventId, String subject, String message);

    /**
     * Send event reminder to all registered attendees
     */
    void sendEventReminder(Long eventId);

    /**
     * Send registration confirmation to individual attendee
     */
    void sendRegistrationConfirmation(String email, String eventName, String eventDate);

    /**
     * Send registration approval notification
     */
    void sendRegistrationApproval(String email, String eventName);
}
