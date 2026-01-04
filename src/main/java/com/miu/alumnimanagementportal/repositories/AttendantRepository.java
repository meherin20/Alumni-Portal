package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Attendant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendantRepository extends JpaRepository<Attendant, Long> {
}