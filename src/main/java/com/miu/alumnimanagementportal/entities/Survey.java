package com.miu.alumnimanagementportal.entities;

import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestion;
import com.miu.alumnimanagementportal.surveys.enums.SurveyStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class Survey extends BaseEntity {

    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    // Optional legacy fields still supported if you already use them
    private String feedback;
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SurveyStatus status = SurveyStatus.DRAFT;

    /**
     * If true, responses do not store respondent identity directly.
     */
    @Column(nullable = false)
    private boolean anonymous = false;

    private LocalDateTime publishAt;
    private LocalDateTime closeAt;

    // Targeting fields (optional)
    private String targetDepartment;
    private Integer targetGraduationYear;
    private String targetCountry;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orderIndex ASC")
    @ToString.Exclude
    private Set<SurveyQuestion> questions = new LinkedHashSet<>();
}

