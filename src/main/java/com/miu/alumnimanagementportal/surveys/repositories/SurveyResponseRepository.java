package com.miu.alumnimanagementportal.surveys.repositories;

import com.miu.alumnimanagementportal.surveys.entities.SurveyResponse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {
    boolean existsBySurveyIdAndUserId(Long surveyId, Long userId);

    Optional<SurveyResponse> findBySurveyIdAndUserId(Long surveyId, Long userId);

    List<SurveyResponse> findBySurveyId(Long surveyId);

    long countBySurveyId(Long surveyId);
}
