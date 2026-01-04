package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.dtos.*;
import com.miu.alumnimanagementportal.entities.Survey;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.DataAlreadyExistException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.SurveyRepository;
import com.miu.alumnimanagementportal.repositories.SurveyResponseRepository;
import com.miu.alumnimanagementportal.services.SurveyResponseService;
import com.miu.alumnimanagementportal.surveys.entities.SurveyAnswer;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestion;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestionOption;
import com.miu.alumnimanagementportal.surveys.entities.SurveyResponse;
import com.miu.alumnimanagementportal.surveys.enums.QuestionType;
import com.miu.alumnimanagementportal.surveys.enums.ResponseStatus;
import com.miu.alumnimanagementportal.surveys.enums.SurveyStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private static final String ANON_SECRET = "change-me";

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository responseRepository;

    @Override
    @Transactional
    public List<SurveySummaryDto> listOpen(String dept, Integer year, String country) {
        List<Survey> list = surveyRepository.findOpenForTarget(
                SurveyStatus.PUBLISHED,
                LocalDateTime.now(),
                dept,
                year,
                country
        );
        return list.stream().map(this::toSummaryDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SurveyDetailDto getSurvey(Long surveyId) {
        Survey survey = surveyRepository.findWithQuestionsById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + surveyId + " not found"));
        // If the survey has no questions attached, this will fail rendering on the frontend; enforce a clear error
        if (survey.getQuestions() == null || survey.getQuestions().isEmpty()) {
            throw new BadRequestException("Survey has no questions configured");
        }
        return toDetailDto(survey);
    }

    @Override
    @Transactional
    public void submitResponse(Long surveyId, SurveyResponseCreateDto dto, boolean anonymous) {
        Survey survey = surveyRepository.findWithQuestionsById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + surveyId + " not found"));

        LocalDateTime now = LocalDateTime.now();
        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new BadRequestException("Survey is not open");
        }
        if (survey.getPublishAt() != null && survey.getPublishAt().isAfter(now)) {
            throw new BadRequestException("Survey not yet published");
        }
        if (survey.getCloseAt() != null && !survey.getCloseAt().isAfter(now)) {
            throw new BadRequestException("Survey is closed");
        }

        String key = dto.getRespondentKey();
        if (key == null || key.isBlank()) {
            throw new BadRequestException("respondentKey is required");
        }

        String anonHash = null;
        if (anonymous || survey.isAnonymous()) {
            anonHash = buildHash(key, surveyId);
            if (responseRepository.existsBySurveyIdAndAnonymousTokenHash(surveyId, anonHash)) {
                throw new DataAlreadyExistException("You have already submitted this survey");
            }
        } else {
            if (responseRepository.existsBySurveyIdAndRespondentKey(surveyId, key)) {
                throw new DataAlreadyExistException("You have already submitted this survey");
            }
        }

        Map<Long, SurveyQuestion> byId = survey.getQuestions().stream()
                .collect(Collectors.toMap(SurveyQuestion::getId, q -> q));

        if (dto.getAnswers() == null || dto.getAnswers().isEmpty()) {
            throw new BadRequestException("Answers are required");
        }

        SurveyResponse response = new SurveyResponse();
        response.setSurvey(survey);
        response.setStatus(ResponseStatus.SUBMITTED);
        response.setStartedAt(LocalDateTime.now());
        response.setSubmittedAt(LocalDateTime.now());
        if (anonymous || survey.isAnonymous()) {
            response.setRespondentKey(null);
            response.setAnonymousTokenHash(anonHash);
        } else {
            response.setRespondentKey(key);
        }

        for (SurveyAnswerRequestDto aDto : dto.getAnswers()) {
            SurveyQuestion q = byId.get(aDto.getQuestionId());
            if (q == null) {
                throw new BadRequestException("Unknown question id " + aDto.getQuestionId());
            }
            SurveyAnswer a = new SurveyAnswer();
            a.setQuestion(q);
            applyAnswer(q, aDto, a);
            response.getAnswers().add(a);
            a.setResponse(response);
        }

        validateRequiredAnswered(survey, response);

        responseRepository.save(response);
    }

    @Override
    public long countSubmitted() {
        return responseRepository.countByStatus(ResponseStatus.SUBMITTED);
    }

    private void applyAnswer(SurveyQuestion q, SurveyAnswerRequestDto dto, SurveyAnswer a) {
        QuestionType type = q.getType();
        switch (type) {
            case SINGLE_CHOICE, YES_NO -> a.setChoiceValue(dto.getChoiceValue());
            case MULTI_CHOICE -> a.setMultiChoiceValues(dto.getMultiChoiceValues());
            case RATING_1_5 -> a.setRatingValue(dto.getRatingValue());
            case TEXT -> a.setTextValue(dto.getTextValue());
        }
    }

    private void validateRequiredAnswered(Survey survey, SurveyResponse response) {
        Map<Long, SurveyAnswer> byQ = response.getAnswers().stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), a -> a, (a, b) -> a));
        for (SurveyQuestion q : survey.getQuestions()) {
            if (!q.isRequired()) continue;
            SurveyAnswer a = byQ.get(q.getId());
            if (a == null) {
                throw new BadRequestException("Required question not answered: " + q.getText());
            }
            switch (q.getType()) {
                case SINGLE_CHOICE, YES_NO -> {
                    if (a.getChoiceValue() == null || a.getChoiceValue().isBlank()) {
                        throw new BadRequestException("Required question not answered: " + q.getText());
                    }
                }
                case MULTI_CHOICE -> {
                    if (a.getMultiChoiceValues() == null || a.getMultiChoiceValues().isBlank()) {
                        throw new BadRequestException("Required question not answered: " + q.getText());
                    }
                }
                case RATING_1_5 -> {
                    if (a.getRatingValue() == null) {
                        throw new BadRequestException("Required question not answered: " + q.getText());
                    }
                }
                case TEXT -> {
                    if (a.getTextValue() == null || a.getTextValue().isBlank()) {
                        throw new BadRequestException("Required question not answered: " + q.getText());
                    }
                }
            }
        }
    }

    private SurveySummaryDto toSummaryDto(Survey s) {
        SurveySummaryDto dto = new SurveySummaryDto();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setStatus(s.getStatus());
        dto.setPublishAt(s.getPublishAt());
        dto.setCloseAt(s.getCloseAt());
        return dto;
    }

    private SurveyDetailDto toDetailDto(Survey s) {
        SurveyDetailDto dto = new SurveyDetailDto();
        dto.setId(s.getId());
        dto.setTitle(s.getTitle());
        dto.setDescription(s.getDescription());
        dto.setStatus(s.getStatus());
        dto.setAnonymous(s.isAnonymous());
        dto.setPublishAt(s.getPublishAt());
        dto.setCloseAt(s.getCloseAt());
        dto.setTargetDepartment(s.getTargetDepartment());
        dto.setTargetGraduationYear(s.getTargetGraduationYear());
        dto.setTargetCountry(s.getTargetCountry());

        List<SurveyQuestionDto> qDtos = new ArrayList<>();
        for (SurveyQuestion q : s.getQuestions()) {
            SurveyQuestionDto qDto = new SurveyQuestionDto();
            qDto.setId(q.getId());
            qDto.setText(q.getText());
            qDto.setType(q.getType());
            qDto.setRequired(q.isRequired());
            qDto.setOrderIndex(q.getOrderIndex());

            List<SurveyQuestionOptionDto> opts = new ArrayList<>();
            for (SurveyQuestionOption opt : q.getOptions()) {
                SurveyQuestionOptionDto oDto = new SurveyQuestionOptionDto();
                oDto.setId(opt.getId());
                oDto.setLabel(opt.getLabel());
                oDto.setValue(opt.getValue());
                oDto.setOrderIndex(opt.getOrderIndex());
                opts.add(oDto);
            }
            qDto.setOptions(opts);
            qDtos.add(qDto);
        }
        dto.setQuestions(qDtos);
        return dto;
    }

    private String buildHash(String key, Long surveyId) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String raw = key + ":" + surveyId + ":" + ANON_SECRET;
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}


