package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}