package com.miu.alumnimanagementportal.surveys.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "survey_question_option")
public class SurveyQuestionOption extends BaseEntity {

    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @ToString.Exclude
    private SurveyQuestion question;

    @Column(nullable = false)
    private String label;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private int orderIndex;
}


