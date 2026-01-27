package com.miu.alumnimanagementportal.surveys.services;

import com.miu.alumnimanagementportal.surveys.dtos.SurveySubmitRequest;

public interface SurveyResponseService {
    void submit(Long surveyId, SurveySubmitRequest request);
    boolean hasUserSubmitted(Long surveyId, String userEmail);
}
