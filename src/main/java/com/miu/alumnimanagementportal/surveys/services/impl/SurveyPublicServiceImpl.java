package com.miu.alumnimanagementportal.surveys.services.impl;

import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyListDto;
import com.miu.alumnimanagementportal.surveys.entities.Survey;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestion;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestionOption;
import com.miu.alumnimanagementportal.surveys.repositories.SurveyRepository;
import com.miu.alumnimanagementportal.surveys.services.SurveyPublicService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyPublicServiceImpl implements SurveyPublicService {

    private final SurveyRepository surveyRepository;

    @Override
    @Transactional
    public List<SurveyListDto> listPublished() {
        return surveyRepository.findPublishedWithQuestions()
                .stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SurveyDetailDto getPublishedSurvey(Long id) {
        Survey survey = surveyRepository.findByIdWithQuestions(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        if (!survey.isPublished()) {
            throw new ResourceNotFoundException("Survey not found");
        }
        return toDetailDto(survey);
    }

    private SurveyListDto toListDto(Survey survey) {
        SurveyListDto dto = new SurveyListDto();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setPublished(survey.isPublished());
        dto.setPublishedAt(survey.getPublishedAt());
        dto.setStartAt(survey.getStartAt());
        dto.setEndAt(survey.getEndAt());
        dto.setQuestionCount(survey.getQuestions() == null ? 0 : survey.getQuestions().size());
        return dto;
    }

    private SurveyDetailDto toDetailDto(Survey survey) {
        SurveyDetailDto dto = new SurveyDetailDto();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setPublished(survey.isPublished());
        dto.setPublishedAt(survey.getPublishedAt());
        dto.setStartAt(survey.getStartAt());
        dto.setEndAt(survey.getEndAt());
        for (SurveyQuestion q : survey.getQuestions()) {
            SurveyDetailDto.SurveyQuestionDto qDto = new SurveyDetailDto.SurveyQuestionDto();
            qDto.setId(q.getId());
            qDto.setQuestionText(q.getQuestionText());
            qDto.setQuestionType(q.getQuestionType());
            qDto.setRequired(q.isRequired());
            qDto.setOptions(q.getOptions().stream().map(SurveyQuestionOption::getLabel).toList());
            dto.getQuestions().add(qDto);
        }
        return dto;
    }
}
