package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.dtos.*;
import com.miu.alumnimanagementportal.entities.Survey;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.SurveyRepository;
import com.miu.alumnimanagementportal.repositories.SurveyResponseRepository;
import com.miu.alumnimanagementportal.services.SurveyAdminService;
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

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyAdminServiceImpl implements SurveyAdminService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository responseRepository;

    @Override
    @Transactional
    public SurveyDetailDto create(SurveyCreateUpdateDto dto) {
        validateQuestions(dto);
        Survey survey = mapToEntity(new Survey(), dto);
        survey.setStatus(SurveyStatus.DRAFT);
        Survey saved = surveyRepository.save(survey);
        return toDetailDto(saved);
    }

    @Override
    @Transactional
    public SurveyDetailDto update(Long id, SurveyCreateUpdateDto dto) {
        Survey survey = surveyRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT surveys can be edited");
        }
        validateQuestions(dto);
        survey.getQuestions().clear();
        mapToEntity(survey, dto);
        Survey saved = surveyRepository.save(survey);
        return toDetailDto(saved);
    }

    @Override
    @Transactional
    public void publish(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
        if (survey.getStatus() != SurveyStatus.DRAFT) {
            throw new BadRequestException("Only DRAFT surveys can be published");
        }
        survey.setStatus(SurveyStatus.PUBLISHED);
        if (survey.getPublishAt() == null) {
            survey.setPublishAt(LocalDateTime.now());
        }
        surveyRepository.save(survey);
    }

    @Override
    @Transactional
    public void close(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
        if (survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new BadRequestException("Only PUBLISHED surveys can be closed");
        }
        survey.setStatus(SurveyStatus.CLOSED);
        if (survey.getCloseAt() == null) {
            survey.setCloseAt(LocalDateTime.now());
        }
        surveyRepository.save(survey);
    }

    @Override
    @Transactional
    public SurveyDetailDto get(Long id) {
        Survey survey = surveyRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
        return toDetailDto(survey);
    }

    @Override
    @Transactional
    public SurveyResultsDto getResults(Long id) {
        Survey survey = surveyRepository.findWithQuestionsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));

        List<SurveyResponse> responses = responseRepository.findBySurveyId(id);
        long submitted = responseRepository.countBySurveyIdAndStatus(id, ResponseStatus.SUBMITTED);

        List<SurveyQuestionResultDto> questionResults = new ArrayList<>();
        for (SurveyQuestion q : survey.getQuestions()) {
            SurveyQuestionResultDto qr = new SurveyQuestionResultDto();
            qr.setQuestionId(q.getId());
            qr.setText(q.getText());
            qr.setType(q.getType());

            switch (q.getType()) {
                case SINGLE_CHOICE, YES_NO -> {
                    Map<String, Long> counts = new HashMap<>();
                    for (SurveyResponse r : responses) {
                        r.getAnswers().stream()
                                .filter(a -> a.getQuestion().getId().equals(q.getId()))
                                .map(SurveyAnswer::getChoiceValue)
                                .filter(Objects::nonNull)
                                .forEach(v -> counts.merge(v, 1L, Long::sum));
                    }
                    qr.setCounts(counts);
                    qr.setPercentages(toPercentages(counts, submitted));
                }
                case MULTI_CHOICE -> {
                    Map<String, Long> counts = new HashMap<>();
                    for (SurveyResponse r : responses) {
                        r.getAnswers().stream()
                                .filter(a -> a.getQuestion().getId().equals(q.getId()))
                                .map(SurveyAnswer::getMultiChoiceValues)
                                .filter(v -> v != null && !v.isBlank())
                                .forEach(v -> {
                                    for (String token : v.split(",")) {
                                        String trimmed = token.trim();
                                        if (!trimmed.isEmpty()) {
                                            counts.merge(trimmed, 1L, Long::sum);
                                        }
                                    }
                                });
                    }
                    qr.setCounts(counts);
                    qr.setPercentages(toPercentages(counts, submitted));
                }
                case RATING_1_5 -> {
                    Map<Integer, Long> dist = new HashMap<>();
                    long total = 0;
                    long sum = 0;
                    for (SurveyResponse r : responses) {
                        r.getAnswers().stream()
                                .filter(a -> a.getQuestion().getId().equals(q.getId()))
                                .map(SurveyAnswer::getRatingValue)
                                .filter(Objects::nonNull)
                                .forEach(v -> dist.merge(v, 1L, Long::sum));
                    }
                    for (Map.Entry<Integer, Long> e : dist.entrySet()) {
                        sum += (long) e.getKey() * e.getValue();
                        total += e.getValue();
                    }
                    qr.setRatingDistribution(dist);
                    qr.setAverageRating(total == 0 ? null : (double) sum / total);
                }
                case TEXT -> {
                    // Text questions: aggregation not computed here; view raw answers separately if needed.
                }
            }
            questionResults.add(qr);
        }

        SurveyResultsDto dto = new SurveyResultsDto();
        dto.setSurveyId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setSubmittedCount(submitted);
        dto.setQuestions(questionResults);
        return dto;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Survey with id " + id + " not found"));
        surveyRepository.delete(survey);
    }

    @Override
    @Transactional
    public List<SurveyDetailDto> listNonAnonymous() {
        return surveyRepository.findByAnonymousFalse()
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<SurveyDetailDto> listAll() {
        return surveyRepository.findAll()
                .stream()
                .map(this::toDetailDto)
                .collect(Collectors.toList());
    }

    private void validateQuestions(SurveyCreateUpdateDto dto) {
        if (dto.getQuestions() == null || dto.getQuestions().isEmpty()) {
            throw new BadRequestException("Survey must contain at least one question");
        }
        for (SurveyQuestionDto q : dto.getQuestions()) {
            if (q.getType() == QuestionType.SINGLE_CHOICE
                    || q.getType() == QuestionType.MULTI_CHOICE
                    || q.getType() == QuestionType.YES_NO) {
                if (q.getOptions() == null || q.getOptions().isEmpty()) {
                    throw new BadRequestException("Choice question must have options: " + q.getText());
                }
            }
        }
    }

    private Survey mapToEntity(Survey survey, SurveyCreateUpdateDto dto) {
        survey.setTitle(dto.getTitle());
        survey.setDescription(dto.getDescription());
        survey.setAnonymous(Boolean.TRUE.equals(dto.getAnonymous()));
        survey.setPublishAt(dto.getPublishAt());
        survey.setCloseAt(dto.getCloseAt());
        survey.setTargetDepartment(dto.getTargetDepartment());
        survey.setTargetGraduationYear(dto.getTargetGraduationYear());
        survey.setTargetCountry(dto.getTargetCountry());

        survey.getQuestions().clear();
        if (dto.getQuestions() != null) {
            for (SurveyQuestionDto qDto : dto.getQuestions()) {
                SurveyQuestion q = new SurveyQuestion();
                q.setSurvey(survey);
                q.setText(qDto.getText());
                q.setType(qDto.getType());
                q.setRequired(Boolean.TRUE.equals(qDto.getRequired()));
                q.setOrderIndex(qDto.getOrderIndex());

                if (qDto.getOptions() != null) {
                    for (SurveyQuestionOptionDto oDto : qDto.getOptions()) {
                        SurveyQuestionOption opt = new SurveyQuestionOption();
                        opt.setQuestion(q);
                        opt.setLabel(oDto.getLabel());
                        opt.setValue(oDto.getValue());
                        opt.setOrderIndex(oDto.getOrderIndex());
                        q.getOptions().add(opt);
                    }
                }
                survey.getQuestions().add(q);
            }
        }
        return survey;
    }

    private SurveyDetailDto toDetailDto(Survey survey) {
        SurveyDetailDto dto = new SurveyDetailDto();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setStatus(survey.getStatus());
        dto.setAnonymous(survey.isAnonymous());
        dto.setPublishAt(survey.getPublishAt());
        dto.setCloseAt(survey.getCloseAt());
        dto.setTargetDepartment(survey.getTargetDepartment());
        dto.setTargetGraduationYear(survey.getTargetGraduationYear());
        dto.setTargetCountry(survey.getTargetCountry());

        List<SurveyQuestionDto> questions = new ArrayList<>();
        for (SurveyQuestion q : survey.getQuestions()) {
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
            questions.add(qDto);
        }
        dto.setQuestions(questions);
        return dto;
    }

    private Map<String, Double> toPercentages(Map<String, Long> counts, long base) {
        if (base <= 0) return Collections.emptyMap();
        Map<String, Double> map = new HashMap<>();
        counts.forEach((k, v) -> map.put(k, (double) v * 100.0 / base));
        return map;
    }
}


