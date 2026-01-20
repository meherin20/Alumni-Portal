package com.miu.alumnimanagementportal.surveys.dtos;

import com.miu.alumnimanagementportal.surveys.enums.SurveyQuestionType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SurveyDetailDto {
    private Long id;
    private String title;
    private String description;
    private boolean published;
    private LocalDateTime publishedAt;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private List<SurveyQuestionDto> questions = new ArrayList<>();

    @Data
    public static class SurveyQuestionDto {
        private Long id;
        private String questionText;
        private SurveyQuestionType questionType;
        private boolean required;
        private List<String> options = new ArrayList<>();
    }
}
