package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.common.enums.EventStatus;
import com.miu.alumnimanagementportal.common.enums.EventType;
import com.miu.alumnimanagementportal.common.enums.RegistrationStatus;
import com.miu.alumnimanagementportal.dtos.EnhancedEventDto;
import com.miu.alumnimanagementportal.dtos.EventDto;
import com.miu.alumnimanagementportal.dtos.EventRegistrationDto;
import com.miu.alumnimanagementportal.entities.Event;
import com.miu.alumnimanagementportal.repositories.EventRepository;
import com.miu.alumnimanagementportal.services.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/events")
public class EventController {
    private final EventService eventService;
    private final EventRepository eventRepository;
    private final Converter converter;

    // Basic CRUD operations
    @PostMapping
    public ResponseEntity<?> createEvent(@RequestBody Map<String, Object> rawData) {
        System.out.println("=== CREATE EVENT REQUEST RECEIVED ===");
        System.out.println("Raw data: " + rawData);

        try {
            // Manual conversion to avoid ModelMapper issues
            EventDto eventDto = new EventDto();
            eventDto.setTitle((String) rawData.get("title"));
            eventDto.setEventType(EventType.valueOf((String) rawData.get("eventType")));
            eventDto.setLocation((String) rawData.get("location"));
            eventDto.setDescription((String) rawData.get("description"));
            eventDto.setHyperlink((String) rawData.get("hyperlink"));

            // Handle dates manually - datetime-local sends ISO format like "2024-12-25T10:30"
            String startDateStr = (String) rawData.get("startDate");
            String endDateStr = (String) rawData.get("endDate");

            if (startDateStr != null && !startDateStr.isEmpty()) {
                try {
                    // Parse the datetime-local string (format: "2024-12-25T10:30")
                    java.time.LocalDateTime localDateTime = java.time.LocalDateTime.parse(startDateStr);
                    eventDto.setStartDate(java.sql.Timestamp.valueOf(localDateTime));
                } catch (Exception e) {
                    System.err.println("Error parsing start date: " + startDateStr + ", error: " + e.getMessage());
                    // Set current date as fallback
                    eventDto.setStartDate(new java.util.Date());
                }
            }

            if (endDateStr != null && !endDateStr.isEmpty()) {
                try {
                    // Parse the datetime-local string (format: "2024-12-25T10:30")
                    eventDto.setEndDate(java.time.LocalDateTime.parse(endDateStr));
                } catch (Exception e) {
                    System.err.println("Error parsing end date: " + endDateStr + ", error: " + e.getMessage());
                    // Set start date + 1 hour as fallback
                    if (eventDto.getStartDate() != null) {
                        java.time.LocalDateTime start = new java.sql.Timestamp(eventDto.getStartDate().getTime()).toLocalDateTime();
                        eventDto.setEndDate(start.plusHours(1));
                    } else {
                        eventDto.setEndDate(java.time.LocalDateTime.now().plusHours(1));
                    }
                }
            }

            Integer maxParticipants = rawData.get("maxParticipants") != null ?
                Integer.valueOf(rawData.get("maxParticipants").toString()) : null;
            eventDto.setMaxParticipants(maxParticipants);

            System.out.println("Converted eventDto: " + eventDto);
            System.out.println("Title: " + eventDto.getTitle());
            System.out.println("Type: " + eventDto.getEventType());
            System.out.println("Location: " + eventDto.getLocation());
            System.out.println("Start Date: " + eventDto.getStartDate());
            System.out.println("End Date: " + eventDto.getEndDate());
            System.out.println("Description: " + eventDto.getDescription());
            System.out.println("Max Participants: " + eventDto.getMaxParticipants());
            System.out.println("Hyperlink: " + eventDto.getHyperlink());
            System.out.println("============================");

            // For debugging, let's try creating a simple test event first
            if ("TEST_EVENT".equals(eventDto.getTitle())) {
                System.out.println("Creating test event...");
                Event testEvent = new Event();
                testEvent.setTitle("Test Event");
                testEvent.setDescription("Test Description");
                testEvent.setEventType(EventType.WORKSHOPS_WEBINARS);
                testEvent.setLocation("Test Location");
                testEvent.setStartDate(new java.util.Date());
                testEvent.setEndDate(java.time.LocalDateTime.now().plusHours(2));
                testEvent.setMaxParticipants(100);
                testEvent.setEventStatus(EventStatus.UPCOMING);
                testEvent.setRegistrationStatus(RegistrationStatus.REGISTER_NOW);

                Event savedTestEvent = eventRepository.save(testEvent);
                System.out.println("Test event saved with ID: " + savedTestEvent.getId());
                return converter.buildResponseEntity(Map.of("success", true, "message", "Test event created successfully", "eventId", savedTestEvent.getId()), HttpStatus.CREATED);
            }

            eventService.create(eventDto);
            System.out.println("Event created successfully!");
            return converter.buildResponseEntity(Map.of("success", true, "message", "Event created successfully"), HttpStatus.CREATED);
        } catch (Exception e) {
            System.err.println("Error creating event: " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return converter.buildResponseEntity(Map.of("success", false, "message", "Failed to create event: " + e.getMessage(), "errorType", e.getClass().getSimpleName()), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getEventAll() {
        try {
            List<EventDto> events = eventService.findAll();
            System.out.println("Found " + events.size() + " basic events");
            return converter.buildResponseEntity(Map.of("success", true, "data", events), HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error getting basic events: " + e.getMessage());
            return converter.buildResponseEntity(Map.of("success", false, "data", List.of()), HttpStatus.OK);
        }
    }

    // Simple debug endpoint to check raw events from database
    @GetMapping("/debug")
    public ResponseEntity<?> debugEvents() {
        try {
            System.out.println("=== DEBUG: Getting raw events from repository ===");
            List<Event> rawEvents = eventRepository.findAll();
            System.out.println("Raw events count: " + rawEvents.size());

            List<Map<String, Object>> debugInfo = rawEvents.stream()
                .map(event -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", event.getId());
                    map.put("title", event.getTitle());
                    map.put("location", event.getLocation());
                    map.put("startDate", event.getStartDate());
                    map.put("endDate", event.getEndDate());
                    map.put("hyperlink", event.getHyperlink());
                    map.put("eventType", event.getEventType() != null ? event.getEventType().name() : null);
                    map.put("eventStatus", event.getEventStatus() != null ? event.getEventStatus().name() : null);
                    map.put("maxParticipants", event.getMaxParticipants());
                    return map;
                })
                .collect(Collectors.toList());

            return converter.buildResponseEntity(Map.of(
                "success", true,
                "message", "Raw events from database",
                "count", rawEvents.size(),
                "events", debugInfo
            ), HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Debug endpoint error: " + e.getMessage());
            e.printStackTrace();
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "error", e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/enhanced")
    public ResponseEntity<?> getAllEnhancedEvents() {
        System.out.println("=== GET ENHANCED EVENTS REQUEST ===");

        try {
            // First, try to get basic events to see if database is working
            List<EventDto> basicEvents = eventService.findAll();
            System.out.println("Found " + basicEvents.size() + " basic events");

            // Now try enhanced conversion
            List<EnhancedEventDto> events = eventService.findAllEnhanced();
            System.out.println("Successfully converted to " + events.size() + " enhanced events");

            return converter.buildResponseEntity(Map.of(
                "success", true,
                "data", events,
                "message", "Events loaded successfully",
                "count", events.size()
            ), HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error getting enhanced events: " + e.getMessage());
            e.printStackTrace();

            // Try to return basic events as fallback
            try {
                List<EventDto> basicEvents = eventService.findAll();
                System.out.println("Fallback: returning " + basicEvents.size() + " basic events");

                return converter.buildResponseEntity(Map.of(
                    "success", true,
                    "data", basicEvents,
                    "message", "Using basic events (enhanced conversion failed)",
                    "count", basicEvents.size()
                ), HttpStatus.OK);
            } catch (Exception fallbackError) {
                System.err.println("Fallback also failed: " + fallbackError.getMessage());

                return converter.buildResponseEntity(Map.of(
                    "success", false,
                    "data", List.of(),
                    "message", "Failed to load events: " + e.getMessage(),
                    "count", 0
                ), HttpStatus.OK);
            }
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUpcomingEvents() {
        return converter.buildResponseEntity(Map.of("data", eventService.findUpcomingEvents()), HttpStatus.OK);
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<?> getEventsByType(@PathVariable EventType eventType) {
        return converter.buildResponseEntity(Map.of("data", eventService.findByType(eventType)), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("data", eventService.getEventById(id)), HttpStatus.OK);
    }

    @GetMapping("/{id}/enhanced")
    public ResponseEntity<?> getEnhancedEventById(@PathVariable Long id) {
        return converter.buildResponseEntity(Map.of("data", eventService.getEnhancedEventById(id)), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEventById(@PathVariable Long id, @Valid @RequestBody EventDto eventDto) {
        return converter.buildResponseEntity(Map.of("data", eventService.update(eventDto, id)), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEventById(@PathVariable Long id) {
        eventService.delete(id);
        return converter.buildResponseEntity(Map.of("message", "Event deleted successfully"), HttpStatus.OK);
    }

    // Registration endpoints
    @PostMapping("/register")
    public ResponseEntity<?> registerForEvent(@Valid @RequestBody EventRegistrationDto registrationDto) {
        eventService.registerForEvent(registrationDto);
        return converter.buildResponseEntity(Map.of("message", "Successfully registered for event"), HttpStatus.OK);
    }

    @DeleteMapping("/{eventId}/register/{userId}")
    public ResponseEntity<?> unregisterFromEvent(@PathVariable Long eventId, @PathVariable Long userId) {
        eventService.unregisterFromEvent(eventId, userId);
        return converter.buildResponseEntity(Map.of("message", "Successfully unregistered from event"), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/registrations")
    public ResponseEntity<?> getEventRegistrations(@PathVariable Long eventId) {
        return converter.buildResponseEntity(Map.of("data", eventService.getEventRegistrations(eventId)), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/is-registered/{userId}")
    public ResponseEntity<?> checkRegistrationStatus(@PathVariable Long eventId, @PathVariable Long userId) {
        boolean isRegistered = eventService.isUserRegisteredForEvent(eventId, userId);
        return converter.buildResponseEntity(Map.of("registered", isRegistered), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/available-seats")
    public ResponseEntity<?> getAvailableSeats(@PathVariable Long eventId) {
        int availableSeats = eventService.getAvailableSeats(eventId);
        return converter.buildResponseEntity(Map.of("availableSeats", availableSeats), HttpStatus.OK);
    }

    // Admin endpoints
    @PutMapping("/{eventId}/approve-registration/{userId}")
    public ResponseEntity<?> approveRegistration(@PathVariable Long eventId, @PathVariable Long userId) {
        eventService.approveRegistration(eventId, userId);
        return converter.buildResponseEntity(Map.of("message", "Registration approved successfully"), HttpStatus.OK);
    }

    @PutMapping("/{eventId}/close-registrations")
    public ResponseEntity<?> closeEventRegistrations(@PathVariable Long eventId) {
        eventService.closeEventRegistrations(eventId);
        return converter.buildResponseEntity(Map.of("message", "Event registrations closed successfully"), HttpStatus.OK);
    }

    @PutMapping("/{eventId}/status/{status}")
    public ResponseEntity<?> updateEventStatus(@PathVariable Long eventId, @PathVariable String status) {
        eventService.updateEventStatus(eventId, status);
        return converter.buildResponseEntity(Map.of("message", "Event status updated successfully"), HttpStatus.OK);
    }

    // Featured event endpoints
    @PutMapping("/{eventId}/toggle-featured")
    public ResponseEntity<?> toggleFeaturedEvent(@PathVariable Long eventId) {
        eventService.toggleFeaturedEvent(eventId);
        return converter.buildResponseEntity(Map.of("message", "Event featured status toggled successfully"), HttpStatus.OK);
    }

    @GetMapping("/featured")
    public ResponseEntity<?> getFeaturedEvents() {
        return converter.buildResponseEntity(Map.of("data", eventService.getFeaturedEvents()), HttpStatus.OK);
    }

    // Notification endpoints
    @PostMapping("/{eventId}/notify")
    public ResponseEntity<?> sendEventNotification(
            @PathVariable Long eventId,
            @RequestBody Map<String, String> notificationData) {
        String subject = notificationData.get("subject");
        String message = notificationData.get("message");

        if (subject == null || message == null) {
            return converter.buildResponseEntity(Map.of("error", "Subject and message are required"), HttpStatus.BAD_REQUEST);
        }

        eventService.sendEventNotification(eventId, subject, message);
        return converter.buildResponseEntity(Map.of("message", "Notification sent successfully"), HttpStatus.OK);
    }

    @PostMapping("/{eventId}/reminder")
    public ResponseEntity<?> sendEventReminder(@PathVariable Long eventId) {
        eventService.sendEventReminder(eventId);
        return converter.buildResponseEntity(Map.of("message", "Event reminder sent successfully"), HttpStatus.OK);
    }

    // Test endpoint for debugging
    @GetMapping("/test")
    public ResponseEntity<?> testEndpoint() {
        try {
            System.out.println("=== TESTING EVENT REPOSITORY ===");
            List<Event> allEvents = eventRepository.findAll();
            System.out.println("Found " + allEvents.size() + " events in repository");

            long count = eventRepository.count();
            System.out.println("Event count: " + count);

            // Return basic event info for debugging
            List<Map<String, Object>> eventInfo = allEvents.stream()
                .map(event -> {
                    Map<String, Object> map = new java.util.HashMap<>();
                    map.put("id", event.getId());
                    map.put("title", event.getTitle());
                    map.put("location", event.getLocation());
                    map.put("startDate", event.getStartDate());
                    map.put("hyperlink", event.getHyperlink());
                    map.put("eventType", event.getEventType());
                    map.put("eventStatus", event.getEventStatus());
                    return map;
                })
                .collect(Collectors.toList());

            return converter.buildResponseEntity(Map.of(
                "success", true,
                "message", "Event API is working!",
                "timestamp", System.currentTimeMillis(),
                "databaseStatus", "Connected",
                "eventCount", count,
                "events", eventInfo,
                "repositoryStatus", "Working"
            ), HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Error in test endpoint: " + e.getMessage());
            e.printStackTrace();

            return converter.buildResponseEntity(Map.of(
                "success", false,
                "error", "Database connection issue: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/test-create")
    public ResponseEntity<?> testCreateEvent() {
        try {
            System.out.println("=== TESTING EVENT CREATION ===");

            EventDto testEvent = new EventDto();
            testEvent.setTitle("Test Event " + System.currentTimeMillis());
            testEvent.setEventType(EventType.ALUMNI_MEETUPS);
            testEvent.setStartDate(new Date());
            testEvent.setEndDate(LocalDateTime.now().plusHours(1)); // 1 hour later
            testEvent.setLocation("Test Location");
            testEvent.setShortDescription("Test short description");
            testEvent.setFullDescription("Test full description");

            System.out.println("Creating test event: " + testEvent.getTitle());
            eventService.create(testEvent);

            return converter.buildResponseEntity(Map.of(
                "message", "Test event created successfully!",
                "eventTitle", testEvent.getTitle(),
                "timestamp", System.currentTimeMillis()
            ), HttpStatus.OK);
        } catch (Exception e) {
            System.err.println("Error in test create: " + e.getMessage());
            e.printStackTrace();
            return converter.buildResponseEntity(Map.of(
                "error", "Failed to create test event: " + e.getMessage(),
                "timestamp", System.currentTimeMillis()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Enhanced registration management
    @GetMapping("/{eventId}/detailed-registrations")
    public ResponseEntity<?> getDetailedEventRegistrations(@PathVariable Long eventId) {
        List<EventRegistrationDto> registrations = eventService.getEventRegistrations(eventId);
        Map<String, Object> response = Map.of(
            "registrations", registrations,
            "total", registrations.size(),
            "confirmed", registrations.stream().filter(reg -> {
                // In a real implementation, you'd check the attendant status
                return true; // Placeholder
            }).count()
        );
        return converter.buildResponseEntity(Map.of("data", response), HttpStatus.OK);
    }
}
