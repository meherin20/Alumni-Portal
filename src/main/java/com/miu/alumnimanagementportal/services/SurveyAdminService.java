package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.*;

public interface SurveyAdminService {

    SurveyDetailDto create(SurveyCreateUpdateDto dto);

    SurveyDetailDto update(Long id, SurveyCreateUpdateDto dto);

    void publish(Long id);

    void close(Long id);

    SurveyDetailDto get(Long id);

    SurveyResultsDto getResults(Long id);

    void delete(Long id);

    java.util.List<SurveyDetailDto> listNonAnonymous();

    java.util.List<SurveyDetailDto> listAll();
}


