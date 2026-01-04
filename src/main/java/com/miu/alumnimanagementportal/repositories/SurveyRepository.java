package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Survey;
import com.miu.alumnimanagementportal.surveys.enums.SurveyStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @EntityGraph(attributePaths = {"questions", "questions.options"})
    Optional<Survey> findWithQuestionsById(Long id);

    @Query("""
        select s from Survey s
        where s.status = :status
          and (s.publishAt is null or s.publishAt <= :now)
          and (s.closeAt is null or s.closeAt > :now)
          and (:dept is null or lower(s.targetDepartment) = lower(:dept))
          and (:year is null or s.targetGraduationYear = :year)
          and (:country is null or lower(s.targetCountry) = lower(:country))
    """)
    List<Survey> findOpenForTarget(
            @Param("status") SurveyStatus status,
            @Param("now") LocalDateTime now,
            @Param("dept") String dept,
            @Param("year") Integer year,
            @Param("country") String country
    );

    List<Survey> findByAnonymousFalse();
}
