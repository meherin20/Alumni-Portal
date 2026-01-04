package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.common.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {

    List<ActivityLog> findTop20ByOrderByAccessTimeDesc();
}