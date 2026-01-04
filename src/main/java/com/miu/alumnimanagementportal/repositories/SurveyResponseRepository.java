package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.surveys.entities.SurveyResponse;
import com.miu.alumnimanagementportal.surveys.enums.ResponseStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SurveyResponseRepository extends JpaRepository<SurveyResponse, Long> {

    boolean existsBySurveyIdAndRespondentKey(Long surveyId, String respondentKey);

    boolean existsBySurveyIdAndAnonymousTokenHash(Long surveyId, String anonymousTokenHash);

    @EntityGraph(attributePaths = {"answers", "answers.question"})
    List<SurveyResponse> findBySurveyId(Long surveyId);

    long countBySurveyIdAndStatus(Long surveyId, ResponseStatus status);

    long countByStatus(ResponseStatus status);
}


