package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.entities.Attendant;
import com.miu.alumnimanagementportal.entities.Event;
import com.miu.alumnimanagementportal.repositories.AttendantRepository;
import com.miu.alumnimanagementportal.repositories.EventRepository;
import com.miu.alumnimanagementportal.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final EventRepository eventRepository;
    private final AttendantRepository attendantRepository;

    @Override
    public void sendEmail(String to, String subject, String message) {
        // TODO: Integrate with email service (SendGrid, AWS SES, etc.)
        // For now, we'll log the email
        System.out.println("=== EMAIL NOTIFICATION ===");
        System.out.println("To: " + to);
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);
        System.out.println("=========================");
    }

    @Override
    public void sendEmail(List<String> toList, String subject, String message) {
        System.out.println("=== BULK EMAIL NOTIFICATION ===");
        System.out.println("Recipients: " + toList.size());
        System.out.println("Subject: " + subject);
        System.out.println("Message: " + message);

        toList.forEach(email -> {
            System.out.println("Sending to: " + email);
            // TODO: Integrate with bulk email service
        });
        System.out.println("================================");
    }

    @Override
    public void sendEventNotification(Long eventId, String subject, String message) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<Attendant> attendees = attendantRepository.findAll().stream()
                .filter(attendant -> attendant.getEvent().getId().equals(eventId) && attendant.isConfirmed())
                .collect(Collectors.toList());

        List<String> emailList = attendees.stream()
                .map(attendant -> attendant.getUser().getEmail())
                .collect(Collectors.toList());

        if (emailList.isEmpty()) {
            System.out.println("No confirmed attendees found for event: " + event.getTitle());
            return;
        }

        sendEmail(emailList, subject, message);
        System.out.println("Event notification sent to " + emailList.size() + " attendees for: " + event.getTitle());
    }

    @Override
    public void sendEventReminder(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        String subject = "Reminder: " + event.getTitle();
        String message = buildReminderMessage(event);

        sendEventNotification(eventId, subject, message);
    }

    @Override
    public void sendRegistrationConfirmation(String email, String eventName, String eventDate) {
        String subject = "Registration Confirmed - " + eventName;
        String message = String.format(
            "Dear Attendee,\n\n" +
            "Your registration for '%s' has been confirmed!\n\n" +
            "Event Details:\n" +
            "Date: %s\n\n" +
            "We look forward to seeing you there.\n\n" +
            "Best regards,\n" +
            "Alumni Management Team",
            eventName, eventDate
        );

        sendEmail(email, subject, message);
    }

    @Override
    public void sendRegistrationApproval(String email, String eventName) {
        String subject = "Registration Approved - " + eventName;
        String message = String.format(
            "Dear Attendee,\n\n" +
            "Great news! Your registration for '%s' has been approved.\n\n" +
            "You will receive further details about the event closer to the date.\n\n" +
            "Best regards,\n" +
            "Alumni Management Team",
            eventName
        );

        sendEmail(email, subject, message);
    }

    private String buildReminderMessage(Event event) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' hh:mm a");
        String formattedDate = event.getStartDate().toString(); // You might want to format this properly

        return String.format(
            "Dear Attendee,\n\n" +
            "This is a friendly reminder about the upcoming event:\n\n" +
            "Event: %s\n" +
            "Date: %s\n" +
            "Location: %s\n" +
            "Description: %s\n\n" +
            "We look forward to seeing you there!\n\n" +
            "Best regards,\n" +
            "Alumni Management Team",
            event.getTitle(),
            formattedDate,
            event.getLocation(),
            event.getShortDescription() != null ? event.getShortDescription() : "Join us for this exciting event"
        );
    }
}
