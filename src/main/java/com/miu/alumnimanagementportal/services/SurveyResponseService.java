package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.dtos.SurveyResponseCreateDto;
import com.miu.alumnimanagementportal.dtos.SurveySummaryDto;

import java.util.List;

public interface SurveyResponseService {

    List<SurveySummaryDto> listOpen(String dept, Integer year, String country);

    SurveyDetailDto getSurvey(Long surveyId);

    void submitResponse(Long surveyId, SurveyResponseCreateDto dto, boolean anonymous);

    long countSubmitted();
}


