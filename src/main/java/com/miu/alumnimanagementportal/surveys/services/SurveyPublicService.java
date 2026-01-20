package com.miu.alumnimanagementportal.surveys.services;

import com.miu.alumnimanagementportal.surveys.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyListDto;

import java.util.List;

public interface SurveyPublicService {
    List<SurveyListDto> listPublished();

    SurveyDetailDto getPublishedSurvey(Long id);
}
