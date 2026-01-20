package com.miu.alumnimanagementportal.surveys.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SurveyListDto {
    private Long id;
    private String title;
    private String description;
    private boolean published;
    private LocalDateTime publishedAt;
    private int questionCount;
    private int responseCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
