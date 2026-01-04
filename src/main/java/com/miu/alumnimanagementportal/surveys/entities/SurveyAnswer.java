package com.miu.alumnimanagementportal.surveys.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "survey_answer")
public class SurveyAnswer extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    private SurveyResponse response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private SurveyQuestion question;

    // SINGLE_CHOICE / YES_NO
    private String choiceValue;

    // MULTI_CHOICE: comma-separated list of values
    @Column(length = 2000)
    private String multiChoiceValues;

    // RATING_1_5
    private Integer ratingValue;

    // TEXT
    @Column(length = 4000)
    private String textValue;
}


