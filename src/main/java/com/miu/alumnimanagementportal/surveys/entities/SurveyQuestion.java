package com.miu.alumnimanagementportal.surveys.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import com.miu.alumnimanagementportal.surveys.enums.SurveyQuestionType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "survey_question")
@Getter
@Setter
@ToString(callSuper = true)
public class SurveyQuestion extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @ToString.Exclude
    private Survey survey;

    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyQuestionType questionType;

    @Column(nullable = false)
    private boolean required = false;

    @Column(nullable = false)
    private int orderIndex;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("orderIndex ASC")
    @org.hibernate.annotations.BatchSize(size = 50)
    @ToString.Exclude
    private List<SurveyQuestionOption> options = new ArrayList<>();
}
