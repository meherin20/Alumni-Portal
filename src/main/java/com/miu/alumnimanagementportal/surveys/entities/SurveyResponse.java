package com.miu.alumnimanagementportal.surveys.entities;

import com.miu.alumnimanagementportal.entities.BaseEntity;
import com.miu.alumnimanagementportal.entities.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
        name = "survey_response",
        uniqueConstraints = @UniqueConstraint(columnNames = {"survey_id", "user_id"})
)
@Getter
@Setter
@ToString(callSuper = true)
public class SurveyResponse extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @ToString.Exclude
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(name = "started_at", nullable = false)
    private LocalDateTime startedAt;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @OneToMany(mappedBy = "response", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<SurveyAnswer> answers = new ArrayList<>();

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        if (startedAt == null) {
            startedAt = now;
        }
        if (submittedAt == null) {
            submittedAt = now;
        }
    }
}
