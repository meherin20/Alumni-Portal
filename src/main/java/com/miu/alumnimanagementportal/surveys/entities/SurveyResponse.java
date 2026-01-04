package com.miu.alumnimanagementportal.surveys.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import com.miu.alumnimanagementportal.entities.Survey;
import com.miu.alumnimanagementportal.surveys.enums.ResponseStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(
        name = "survey_response",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_response_survey_respondent",
                        columnNames = {"survey_id", "respondentKey"}
                ),
                @UniqueConstraint(
                        name = "uk_response_survey_anon_hash",
                        columnNames = {"survey_id", "anonymousTokenHash"}
                )
        }
)
public class SurveyResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    private Survey survey;

    /**
     * For non-anonymous surveys: a logical respondent identifier (e.g. email).
     * Null when survey is anonymous.
     */
    @Column(length = 255)
    private String respondentKey;

    /**
     * For anonymous surveys: hash token used to enforce 1 submission per user.
     */
    @Column(length = 128)
    private String anonymousTokenHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResponseStatus status = ResponseStatus.IN_PROGRESS;

    @Column(nullable = false)
    private LocalDateTime startedAt = LocalDateTime.now();

    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SurveyAnswer> answers = new ArrayList<>();
}


