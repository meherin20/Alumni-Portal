package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.common.enums.EventType;
import com.miu.alumnimanagementportal.dtos.EnhancedEventDto;
import com.miu.alumnimanagementportal.dtos.EventDto;
import com.miu.alumnimanagementportal.dtos.EventRegistrationDto;

import java.util.List;

public interface EventService {
    List<EventDto> findAll();
    List<EnhancedEventDto> findAllEnhanced();
    List<EnhancedEventDto> findByType(EventType eventType);
    List<EnhancedEventDto> findUpcomingEvents();
    void create(EventDto eventDto);
    EventDto update(EventDto eventDto, Long id);
    EnhancedEventDto getEnhancedEventById(Long id);
    EventDto getEventById(Long id);
    void delete(Long id);

    // Registration methods
    void registerForEvent(EventRegistrationDto registrationDto);
    void unregisterFromEvent(Long eventId, Long userId);
    List<EventRegistrationDto> getEventRegistrations(Long eventId);
    boolean isUserRegisteredForEvent(Long eventId, Long userId);

    // Admin methods
    void approveRegistration(Long eventId, Long userId);
    void closeEventRegistrations(Long eventId);
    void updateEventStatus(Long eventId, String status);
    void toggleFeaturedEvent(Long eventId);
    List<EnhancedEventDto> getFeaturedEvents();
    void sendEventNotification(Long eventId, String subject, String message);
    void sendEventReminder(Long eventId);
    int getAvailableSeats(Long eventId);
}
