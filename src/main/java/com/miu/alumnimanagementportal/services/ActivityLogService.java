package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.ActivityLogDto;

import java.util.List;

public interface ActivityLogService {
    ActivityLogDto createActivityLog(ActivityLogDto activityLog);
    ActivityLogDto updateActivityLog(ActivityLogDto activityLog, Long id);
    ActivityLogDto findById(Long id);
    void deleteById(Long id);
    List<ActivityLogDto> findAll();
}