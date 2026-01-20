package com.miu.alumnimanagementportal.surveys.dtos;

import com.miu.alumnimanagementportal.surveys.enums.SurveyQuestionType;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SurveyResultsDto {
    private Long surveyId;
    private String title;
    private List<QuestionResult> questions = new ArrayList<>();

    @Data
    public static class QuestionResult {
        private Long questionId;
        private String questionText;
        private SurveyQuestionType questionType;
        private long totalResponses;
        private Map<String, Long> optionCounts;
        private Map<Integer, Long> ratingDistribution;
        private Double ratingAverage;
        private List<String> textAnswers;
    }
}
