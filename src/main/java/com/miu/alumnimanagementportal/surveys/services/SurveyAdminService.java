package com.miu.alumnimanagementportal.surveys.services;

import com.miu.alumnimanagementportal.surveys.dtos.SurveyCreateRequest;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyListDto;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyResultsDto;

import java.util.List;

public interface SurveyAdminService {
    SurveyDetailDto create(String adminEmail, SurveyCreateRequest request);

    SurveyDetailDto update(String adminEmail, Long surveyId, SurveyCreateRequest request);

    void delete(String adminEmail, Long surveyId);

    void publish(String adminEmail, Long surveyId);

    void unpublish(String adminEmail, Long surveyId);

    List<SurveyListDto> listAll(String adminEmail);

    SurveyDetailDto getDetail(String adminEmail, Long surveyId);

    SurveyResultsDto getResults(String adminEmail, Long surveyId);
}
