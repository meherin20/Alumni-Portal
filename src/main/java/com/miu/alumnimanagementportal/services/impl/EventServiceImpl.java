package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.common.enums.EventStatus;
import com.miu.alumnimanagementportal.common.enums.EventType;
import com.miu.alumnimanagementportal.common.enums.RegistrationStatus;
import com.miu.alumnimanagementportal.dtos.EnhancedEventDto;
import com.miu.alumnimanagementportal.dtos.EventDto;
import com.miu.alumnimanagementportal.dtos.EventRegistrationDto;
import com.miu.alumnimanagementportal.entities.Attendant;
import com.miu.alumnimanagementportal.entities.Event;
import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.AttendantRepository;
import com.miu.alumnimanagementportal.repositories.EventRepository;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.services.EventService;
import com.miu.alumnimanagementportal.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final AttendantRepository attendantRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final Converter converter;

    @Override
    public List<EventDto> findAll() {
        return eventRepository.findAll().stream()
                .map(element -> converter.convert(element, EventDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<EnhancedEventDto> findAllEnhanced() {
        try {
            System.out.println("=== FIND ALL ENHANCED EVENTS ===");
            List<Event> events = eventRepository.findAll();
            System.out.println("Found " + events.size() + " events in repository");

            if (events.isEmpty()) {
                System.out.println("No events found in database");
                return List.of();
            }

            List<EnhancedEventDto> enhancedEvents = new java.util.ArrayList<>();

            for (Event event : events) {
                try {
                    EnhancedEventDto dto = convertToEnhancedEventDto(event);
                    enhancedEvents.add(dto);
                } catch (Exception e) {
                    System.err.println("Failed to convert event " + event.getId() + ": " + e.getMessage());
                    // Create a basic fallback for this event
                    EnhancedEventDto fallback = new EnhancedEventDto();
                    fallback.setId(event.getId());
                    fallback.setTitle(event.getTitle() != null ? event.getTitle() : "Untitled Event");
                    fallback.setLocation(event.getLocation() != null ? event.getLocation() : "TBD");
                    fallback.setEventStatus(EventStatus.UPCOMING);
                    fallback.setRegistrationStatus(RegistrationStatus.REGISTER_NOW);
                    fallback.setFeatured(false);
                    enhancedEvents.add(fallback);
                }
            }

            System.out.println("Successfully converted to " + enhancedEvents.size() + " enhanced events");
            return enhancedEvents;
        } catch (Exception e) {
            System.err.println("Error in findAllEnhanced: " + e.getMessage());
            e.printStackTrace();
            // Return empty list instead of throwing exception
            return List.of();
        }
    }

    @Override
    public List<EnhancedEventDto> findByType(EventType eventType) {
        return eventRepository.findAll().stream()
                .filter(event -> event.getEventType().equals(eventType))
                .map(this::convertToEnhancedEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<EnhancedEventDto> findUpcomingEvents() {
        Date now = new Date();
        return eventRepository.findAll().stream()
                .filter(event -> event.getStartDate().after(now) && event.getEventStatus() == EventStatus.UPCOMING)
                .map(this::convertToEnhancedEventDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void create(EventDto eventDto) {
        System.out.println("=== EVENT SERVICE CREATE ===");
        System.out.println("Converting EventDto to Event...");
        System.out.println("EventDto title: " + eventDto.getTitle());
        System.out.println("EventDto type: " + eventDto.getEventType());
        System.out.println("EventDto location: " + eventDto.getLocation());
        System.out.println("EventDto startDate: " + eventDto.getStartDate());

        Optional.ofNullable(eventDto.getId()).ifPresent(id -> {
            if (eventRepository.existsById(id)) {
                throw new DataAlreadyExistException("Event with id " + id + " already exists");
            }
        });

        try {
            System.out.println("Starting manual event creation...");
            Event event = new Event();
            event.setTitle(eventDto.getTitle());
            event.setDescription(eventDto.getDescription());
            event.setEventType(eventDto.getEventType());
            event.setLocation(eventDto.getLocation());
            event.setStartDate(eventDto.getStartDate());
            event.setEndDate(eventDto.getEndDate());
            event.setMaxParticipants(eventDto.getMaxParticipants());
            event.setHyperlink(eventDto.getHyperlink());
            // Set default values for status fields
            event.setEventStatus(EventStatus.UPCOMING);
            event.setRegistrationStatus(RegistrationStatus.REGISTER_NOW);

            System.out.println("Manual event creation completed. Event title: " + event.getTitle());
            System.out.println("Event location: " + event.getLocation());
            System.out.println("Event startDate: " + event.getStartDate());
            System.out.println("Event endDate: " + event.getEndDate());
            System.out.println("Event eventType: " + event.getEventType());
            System.out.println("Event hyperlink: " + event.getHyperlink());
            System.out.println("Event maxParticipants: " + event.getMaxParticipants());

            System.out.println("Saving to repository...");
            Event savedEvent = eventRepository.save(event);
            System.out.println("Event saved successfully with ID: " + savedEvent.getId());

            // Verify the event was saved
            Optional<Event> verifyEvent = eventRepository.findById(savedEvent.getId());
            System.out.println("Verification: Event exists in DB: " + verifyEvent.isPresent());

        } catch (Exception e) {
            System.err.println("Error in create method: " + e.getMessage());
            System.err.println("Error type: " + e.getClass().getName());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public EventDto update(EventDto eventDto, Long id) {
        return Optional.ofNullable(eventDto.getId()).map(entityId -> {
            if (!eventRepository.existsById(entityId)) {
                throw new ResourceNotFoundException("Event with id " + entityId + " not found");
            }
            Event event = converter.convert(eventDto, Event.class);
            return converter.convert(eventRepository.save(event), EventDto.class);
        }).orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    public EnhancedEventDto getEnhancedEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
        return convertToEnhancedEventDto(event);
    }

    @Override
    public EventDto getEventById(Long id) {
        return Optional.ofNullable(id)
                .map(eventRepository::findById)
                .map(element -> converter.convert(element, EventDto.class))
                .orElseThrow(() -> new ResourceNotFoundException("Event with id " + id + " not found"));
    }

    @Override
    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event with id " + id + " not found");
        }
        eventRepository.deleteById(id);
    }

    @Override
    public void registerForEvent(EventRegistrationDto registrationDto) {
        Event event = eventRepository.findById(registrationDto.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findById(registrationDto.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already registered
        boolean alreadyRegistered = attendantRepository.findAll().stream()
                .anyMatch(attendant -> attendant.getEvent().getId().equals(registrationDto.getEventId())
                        && attendant.getUser().getId().equals(registrationDto.getUserId()));

        if (alreadyRegistered) {
            throw new DataAlreadyExistException("User is already registered for this event");
        }

        // Check capacity
        if (event.getMaxParticipants() != null &&
            attendantRepository.findAll().stream()
                .filter(attendant -> attendant.getEvent().getId().equals(registrationDto.getEventId()))
                .count() >= event.getMaxParticipants()) {
            throw new DataAlreadyExistException("Event is at full capacity");
        }

        Attendant attendant = new Attendant();
        attendant.setEvent(event);
        attendant.setUser(user);
        attendant.setConfirmed(false);

        attendantRepository.save(attendant);

        // Send registration confirmation email
        notificationService.sendRegistrationConfirmation(
            user.getEmail(),
            event.getTitle(),
            event.getStartDate().toString()
        );
    }

    @Override
    public void unregisterFromEvent(Long eventId, Long userId) {
        Attendant attendant = attendantRepository.findAll().stream()
                .filter(a -> a.getEvent().getId().equals(eventId) && a.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        attendantRepository.delete(attendant);
    }

    @Override
    public List<EventRegistrationDto> getEventRegistrations(Long eventId) {
        return attendantRepository.findAll().stream()
                .filter(attendant -> attendant.getEvent().getId().equals(eventId))
                .map(attendant -> {
                    EventRegistrationDto dto = new EventRegistrationDto();
                    dto.setEventId(eventId);
                    dto.setUserId(attendant.getUser().getId());
                    dto.setUserName(attendant.getUser().getFirstName() + " " + attendant.getUser().getLastName());
                    dto.setUserEmail(attendant.getUser().getEmail());
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    public boolean isUserRegisteredForEvent(Long eventId, Long userId) {
        return attendantRepository.findAll().stream()
                .anyMatch(attendant -> attendant.getEvent().getId().equals(eventId)
                        && attendant.getUser().getId().equals(userId));
    }

    @Override
    public void approveRegistration(Long eventId, Long userId) {
        Attendant attendant = attendantRepository.findAll().stream()
                .filter(a -> a.getEvent().getId().equals(eventId) && a.getUser().getId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        Event event = attendant.getEvent();
        attendant.setConfirmed(true);
        attendantRepository.save(attendant);

        // Send approval notification
        notificationService.sendRegistrationApproval(
            attendant.getUser().getEmail(),
            event.getTitle()
        );
    }

    @Override
    public void closeEventRegistrations(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setRegistrationStatus(RegistrationStatus.CLOSED);
        eventRepository.save(event);
    }

    @Override
    public void updateEventStatus(Long eventId, String status) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        try {
            EventStatus eventStatus = EventStatus.valueOf(status.toUpperCase());
            event.setEventStatus(eventStatus);
            eventRepository.save(event);
        } catch (IllegalArgumentException e) {
            throw new ResourceNotFoundException("Invalid event status: " + status);
        }
    }

    @Override
    public void toggleFeaturedEvent(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.setFeatured(!event.isFeatured());
        eventRepository.save(event);
    }

    @Override
    public List<EnhancedEventDto> getFeaturedEvents() {
        return eventRepository.findAll().stream()
                .filter(Event::isFeatured)
                .map(this::convertToEnhancedEventDto)
                .collect(Collectors.toList());
    }

    @Override
    public void sendEventNotification(Long eventId, String subject, String message) {
        notificationService.sendEventNotification(eventId, subject, message);
    }

    @Override
    public void sendEventReminder(Long eventId) {
        notificationService.sendEventReminder(eventId);
    }

    @Override
    public int getAvailableSeats(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        if (event.getMaxParticipants() == null) {
            return Integer.MAX_VALUE; // Unlimited seats
        }

        long registeredCount = attendantRepository.findAll().stream()
                .filter(attendant -> attendant.getEvent().getId().equals(eventId))
                .count();

        return Math.max(0, event.getMaxParticipants() - (int) registeredCount);
    }

    private EnhancedEventDto convertToEnhancedEventDto(Event event) {
        try {
            System.out.println("Converting event: " + event.getId() + " - " + event.getTitle());

            EnhancedEventDto dto = converter.convert(event, EnhancedEventDto.class);

            // Set computed fields with null checks
            if (event.getEventType() != null) {
                dto.setEventTypeLabel(event.getEventType().getLabel());
            }

            if (event.getEventStatus() != null) {
                dto.setEventStatusLabel(event.getEventStatus().getLabel());
            }

            if (event.getRegistrationStatus() != null) {
                dto.setRegistrationStatusLabel(event.getRegistrationStatus().getLabel());
            }

            // Set organizer names with null check
            if (event.getOrganizers() != null && !event.getOrganizers().isEmpty()) {
                dto.setOrganizerNames(event.getOrganizers().stream()
                        .filter(user -> user != null && user.getFirstName() != null && user.getLastName() != null)
                        .map(user -> user.getFirstName() + " " + user.getLastName())
                        .collect(Collectors.toList()));
            } else {
                dto.setOrganizerNames(List.of("Alumni Office"));
            }

            // Set current participants (simplified to avoid repository issues)
            try {
                long currentParticipants = attendantRepository.findAll().stream()
                        .filter(attendant -> attendant != null && attendant.getEvent() != null && attendant.getEvent().getId().equals(event.getId()))
                        .count();
                dto.setCurrentParticipants((int) currentParticipants);

                if (event.getMaxParticipants() != null) {
                    dto.setSeatsAvailable(Math.max(0, event.getMaxParticipants() - (int) currentParticipants));
                } else {
                    dto.setSeatsAvailable(null);
                }
            } catch (Exception e) {
                // If attendant repository fails, set defaults
                dto.setCurrentParticipants(0);
                dto.setSeatsAvailable(event.getMaxParticipants());
            }

            // Update event status based on dates
            LocalDateTime now = LocalDateTime.now();
            if (event.getStartDate() != null && event.getStartDate().toInstant().isAfter(java.time.Instant.now())) {
                dto.setEventStatus(EventStatus.UPCOMING);
            } else if (event.getEndDate() != null && event.getEndDate().isAfter(now)) {
                dto.setEventStatus(EventStatus.ONGOING);
            } else {
                dto.setEventStatus(EventStatus.PAST);
            }

            // Set featured status with null check
            dto.setFeatured(event.isFeatured());

            // Ensure required fields are not null
            if (dto.getTitle() == null) dto.setTitle("Untitled Event");
            if (dto.getLocation() == null) dto.setLocation("TBD");
            if (dto.getShortDescription() == null) dto.setShortDescription("");
            if (dto.getFullDescription() == null) dto.setFullDescription("");
            if (dto.getMeetingLink() == null) dto.setMeetingLink("");
            if (dto.getAgenda() == null) dto.setAgenda("");
            if (dto.getPosterUrl() == null) dto.setPosterUrl("");
            if (dto.getAttachments() == null) dto.setAttachments("");
            if (dto.getSpeakers() == null) dto.setSpeakers("");

            System.out.println("Successfully converted event: " + event.getId());
            return dto;

        } catch (Exception e) {
            System.err.println("Error converting event " + event.getId() + ": " + e.getMessage());
            e.printStackTrace();

            // Return a minimal DTO to prevent the entire request from failing
            EnhancedEventDto fallback = new EnhancedEventDto();
            fallback.setId(event.getId());
            fallback.setTitle(event.getTitle() != null ? event.getTitle() : "Error loading event");
            fallback.setLocation(event.getLocation() != null ? event.getLocation() : "TBD");
            fallback.setEventStatus(EventStatus.UPCOMING);
            fallback.setRegistrationStatus(RegistrationStatus.REGISTER_NOW);
            fallback.setFeatured(false);
            return fallback;
        }
    }
}
