package com.miu.alumnimanagementportal.dtos;

import com.miu.alumnimanagementportal.surveys.enums.QuestionType;
import lombok.Data;

import java.util.Map;

@Data
public class SurveyQuestionResultDto {

    private Long questionId;
    private String text;
    private QuestionType type;

    private Map<String, Long> counts;
    private Map<String, Double> percentages;

    private Double averageRating;
    private Map<Integer, Long> ratingDistribution;
}


