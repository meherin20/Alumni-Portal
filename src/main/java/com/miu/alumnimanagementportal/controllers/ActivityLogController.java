package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.ActivityLogDto;
import com.miu.alumnimanagementportal.repositories.ActivityLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/activity-logs")
@RequiredArgsConstructor
public class ActivityLogController {

    private final ActivityLogRepository activityLogRepository;
    private final Converter converter;

    @GetMapping("/recent")
    public ResponseEntity<?> recent(@RequestParam(name = "limit", defaultValue = "20") int limit) {
        // Repository method is fixed at 20; limit param is kept for future extension
        List<ActivityLogDto> data = converter.convertList(
                activityLogRepository.findTop20ByOrderByAccessTimeDesc(),
                ActivityLogDto.class
        );
        return new ResponseEntity<>(Map.of("data", data), HttpStatus.OK);
    }
}


