package com.miu.alumnimanagementportal.surveys.services.impl;

import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyCreateRequest;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyDetailDto;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyListDto;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyResultsDto;
import com.miu.alumnimanagementportal.surveys.entities.Survey;
import com.miu.alumnimanagementportal.surveys.entities.SurveyAnswer;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestion;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestionOption;
import com.miu.alumnimanagementportal.surveys.entities.SurveyResponse;
import com.miu.alumnimanagementportal.surveys.enums.SurveyQuestionType;
import com.miu.alumnimanagementportal.surveys.repositories.SurveyRepository;
import com.miu.alumnimanagementportal.surveys.repositories.SurveyResponseRepository;
import com.miu.alumnimanagementportal.surveys.services.SurveyAdminService;
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
    private final UserRepository userRepository;

    @Override
    @Transactional
    public SurveyDetailDto create(String adminEmail, SurveyCreateRequest request) {
        User admin = requireAdmin(adminEmail);
        validateSurvey(request);
        Survey survey = mapToEntity(new Survey(), request);
        
        // Final validation before save - ensure all questions have questionText
        for (int i = 0; i < survey.getQuestions().size(); i++) {
            SurveyQuestion q = survey.getQuestions().get(i);
            if (q.getQuestionText() == null || q.getQuestionText().trim().isEmpty()) {
                throw new BadRequestException("Question " + (i + 1) + " has null or empty text before save");
            }
        }
        
        survey.setPublished(false);
        Survey saved = surveyRepository.save(survey);
        return toDetailDto(saved);
    }

    @Override
    @Transactional
    public SurveyDetailDto update(String adminEmail, Long surveyId, SurveyCreateRequest request) {
        User admin = requireAdmin(adminEmail);
        Survey survey = surveyRepository.findByIdWithQuestions(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        validateSurvey(request);
        survey.getQuestions().clear();
        mapToEntity(survey, request);
        Survey saved = surveyRepository.save(survey);
        return toDetailDto(saved);
    }

    @Override
    @Transactional
    public void delete(String adminEmail, Long surveyId) {
        User admin = requireAdmin(adminEmail);
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        surveyRepository.delete(survey);
    }

    @Override
    @Transactional
    public void publish(String adminEmail, Long surveyId) {
        User admin = requireAdmin(adminEmail);
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        if (!survey.isPublished()) {
            survey.setPublished(true);
            survey.setPublishedAt(LocalDateTime.now());
            surveyRepository.save(survey);
        }
    }

    @Override
    @Transactional
    public void unpublish(String adminEmail, Long surveyId) {
        User admin = requireAdmin(adminEmail);
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        if (survey.isPublished()) {
            survey.setPublished(false);
            survey.setPublishedAt(null);
            surveyRepository.save(survey);
        }
    }

    @Override
    @Transactional
    public int publishAllDraft(String adminEmail) {
        User admin = requireAdmin(adminEmail);
        List<Survey> draftSurveys = surveyRepository.findAllWithQuestions()
                .stream()
                .filter(s -> !s.isPublished())
                .collect(Collectors.toList());
        
        LocalDateTime now = LocalDateTime.now();
        for (Survey survey : draftSurveys) {
            survey.setPublished(true);
            survey.setPublishedAt(now);
            surveyRepository.save(survey);
        }
        
        return draftSurveys.size();
    }

    @Override
    @Transactional
    public List<SurveyListDto> listAll(String adminEmail) {
        User admin = requireAdmin(adminEmail);
        return surveyRepository.findAllWithQuestions()
                .stream()
                .map(this::toListDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SurveyDetailDto getDetail(String adminEmail, Long surveyId) {
        User admin = requireAdmin(adminEmail);
        Survey survey = surveyRepository.findByIdWithQuestions(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        return toDetailDto(survey);
    }

    @Override
    @Transactional
    public SurveyResultsDto getResults(String adminEmail, Long surveyId) {
        User admin = requireAdmin(adminEmail);
        Survey survey = surveyRepository.findByIdWithQuestions(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        List<SurveyResponse> responses = responseRepository.findBySurveyId(surveyId);

        SurveyResultsDto results = new SurveyResultsDto();
        results.setSurveyId(survey.getId());
        results.setTitle(survey.getTitle());
        results.setTotalResponses((long) responses.size());

        for (SurveyQuestion question : survey.getQuestions()) {
            SurveyResultsDto.QuestionResult qr = new SurveyResultsDto.QuestionResult();
            qr.setQuestionId(question.getId());
            qr.setQuestionText(question.getQuestionText());
            qr.setQuestionType(question.getQuestionType());

            List<SurveyAnswer> answers = responses.stream()
                    .flatMap(r -> r.getAnswers().stream())
                    .filter(a -> a.getQuestion().getId().equals(question.getId()))
                    .toList();
            qr.setTotalResponses(answers.size());

            if (question.getQuestionType() == SurveyQuestionType.MCQ
                    || question.getQuestionType() == SurveyQuestionType.CHECKBOX) {
                Map<String, Long> counts = new LinkedHashMap<>();
                for (SurveyQuestionOption opt : question.getOptions()) {
                    counts.put(opt.getLabel(), 0L);
                }
                for (SurveyAnswer answer : answers) {
                    String value = answer.getAnswerValue();
                    if (value == null) continue;
                    for (String token : value.split(",")) {
                        String trimmed = token.trim();
                        if (trimmed.isEmpty()) continue;
                        counts.put(trimmed, counts.getOrDefault(trimmed, 0L) + 1);
                    }
                }
                qr.setOptionCounts(counts);
            } else if (question.getQuestionType() == SurveyQuestionType.RATING) {
                Map<Integer, Long> dist = new LinkedHashMap<>();
                for (int i = 1; i <= 5; i++) {
                    dist.put(i, 0L);
                }
                long total = 0;
                long sum = 0;
                for (SurveyAnswer answer : answers) {
                    try {
                        int rating = Integer.parseInt(answer.getAnswerValue());
                        if (rating >= 1 && rating <= 5) {
                            dist.put(rating, dist.get(rating) + 1);
                            total++;
                            sum += rating;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
                qr.setRatingDistribution(dist);
                qr.setRatingAverage(total == 0 ? null : (double) sum / total);
            } else {
                List<String> textAnswers = answers.stream()
                        .map(SurveyAnswer::getAnswerValue)
                        .filter(v -> v != null && !v.isBlank())
                        .toList();
                qr.setTextAnswers(textAnswers);
            }
            results.getQuestions().add(qr);
        }

        // Populate user responses with user information
        Map<Long, SurveyResultsDto.UserResponse> userResponseMap = new LinkedHashMap<>();
        for (SurveyResponse response : responses) {
            User user = response.getUser();
            Long userId = user.getId();
            
            SurveyResultsDto.UserResponse userResponse = userResponseMap.computeIfAbsent(userId, k -> {
                SurveyResultsDto.UserResponse ur = new SurveyResultsDto.UserResponse();
                ur.setUserId(user.getId());
                ur.setUserEmail(user.getEmail());
                ur.setUserName(user.getFirstName() + " " + user.getLastName());
                return ur;
            });

            // Add all answers for this user
            for (SurveyAnswer answer : response.getAnswers()) {
                SurveyResultsDto.AnswerDetail answerDetail = new SurveyResultsDto.AnswerDetail();
                answerDetail.setQuestionId(answer.getQuestion().getId());
                answerDetail.setQuestionText(answer.getQuestion().getQuestionText());
                answerDetail.setAnswerValue(answer.getAnswerValue());
                userResponse.getAnswers().add(answerDetail);
            }
        }
        results.setUserResponses(new ArrayList<>(userResponseMap.values()));

        return results;
    }

    private User requireAdmin(String email) {
        if (email == null || email.isBlank()) {
            throw new BadRequestException("Admin email is required");
        }
        User user = userRepository.findByEmail(email.trim());
        if (user == null || user.getRole() == null || user.getRole().getTitle() == null) {
            throw new BadRequestException("Admin not found");
        }
        if (!"ADMIN".equalsIgnoreCase(user.getRole().getTitle())) {
            throw new BadRequestException("Only admin can perform this action");
        }
        return user;
    }

    private void validateSurvey(SurveyCreateRequest request) {
        if (request.getStartAt() != null && request.getEndAt() != null) {
            if (request.getEndAt().isBefore(request.getStartAt())) {
                throw new BadRequestException("End time must be after start time");
            }
        }
        if (request.getQuestions() == null || request.getQuestions().isEmpty()) {
            throw new BadRequestException("Survey must have at least one question");
        }
        request.getQuestions().forEach(q -> {
            if (q.getQuestionText() == null || q.getQuestionText().trim().isEmpty()) {
                throw new BadRequestException("Question text is required");
            }
            if (q.getQuestionType() == null) {
                throw new BadRequestException("Question type is required");
            }
            if (q.getRequired() == null) {
                throw new BadRequestException("Question required flag is required");
            }
            if (q.getQuestionType() == SurveyQuestionType.MCQ
                    || q.getQuestionType() == SurveyQuestionType.CHECKBOX) {
                if (q.getOptions() == null || q.getOptions().isEmpty()) {
                    throw new BadRequestException("Options are required for choice questions");
                }
                q.getOptions().forEach(opt -> {
                    if (opt.getLabel() == null || opt.getLabel().trim().isEmpty()) {
                        throw new BadRequestException("Option label is required");
                    }
                });
            }
        });
    }

    private Survey mapToEntity(Survey survey, SurveyCreateRequest request) {
        survey.setTitle(request.getTitle().trim());
        survey.setDescription(request.getDescription());
        survey.setStartAt(request.getStartAt());
        survey.setEndAt(request.getEndAt());
        survey.setAnonymous(false);
        survey.getQuestions().clear();

        int qIndex = 0;
        for (var qReq : request.getQuestions()) {
            if (qReq == null) {
                throw new BadRequestException("Question " + (qIndex + 1) + " is null");
            }
            
            String questionText = qReq.getQuestionText();
            if (questionText == null) {
                throw new BadRequestException("Question text is null for question " + (qIndex + 1));
            }
            
            String trimmedText = questionText.trim();
            if (trimmedText.isEmpty()) {
                throw new BadRequestException("Question text cannot be empty for question " + (qIndex + 1));
            }
            
            SurveyQuestion question = new SurveyQuestion();
            question.setSurvey(survey);
            question.setQuestionText(trimmedText);
            
            // Final safety check - ensure it was set
            String verifyText = question.getQuestionText();
            if (verifyText == null || verifyText.isEmpty()) {
                throw new BadRequestException("Failed to set question text for question " + (qIndex + 1) + ". Text was: '" + trimmedText + "'");
            }
            
            // Log for debugging
            System.out.println("[DEBUG] Question " + (qIndex + 1) + " - Setting questionText: '" + verifyText + "' (length: " + verifyText.length() + ")");
            
            if (qReq.getQuestionType() == null) {
                throw new BadRequestException("Question type is null for question " + (qIndex + 1));
            }
            
            question.setQuestionType(qReq.getQuestionType());
            question.setRequired(Boolean.TRUE.equals(qReq.getRequired()));
            question.setOrderIndex(qIndex++);

            if (qReq.getQuestionType() == SurveyQuestionType.MCQ
                    || qReq.getQuestionType() == SurveyQuestionType.CHECKBOX) {
                int oIndex = 0;
                for (var optReq : qReq.getOptions()) {
                    String labelText = optReq.getLabel() != null ? optReq.getLabel().trim() : "";
                    if (labelText.isEmpty()) {
                        throw new BadRequestException("Option label cannot be empty for question " + (qIndex + 1) + ", option " + (oIndex + 1));
                    }
                    SurveyQuestionOption option = new SurveyQuestionOption();
                    option.setQuestion(question);
                    option.setLabel(labelText);
                    option.setValue(labelText); // Set value same as label for now
                    option.setOrderIndex(oIndex++);
                    question.getOptions().add(option);
                }
            }
            survey.getQuestions().add(question);
        }
        return survey;
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

    private SurveyListDto toListDto(Survey survey) {
        SurveyListDto dto = new SurveyListDto();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setPublished(survey.isPublished());
        dto.setPublishedAt(survey.getPublishedAt());
        dto.setStartAt(survey.getStartAt());
        dto.setEndAt(survey.getEndAt());
        
        // Safely get question count - questions should be loaded by EntityGraph
        try {
            dto.setQuestionCount(survey.getQuestions() == null ? 0 : survey.getQuestions().size());
        } catch (Exception e) {
            // Fallback if lazy loading fails
            dto.setQuestionCount(0);
        }
        
        // Get response count
        try {
            long responseCount = responseRepository.countBySurveyId(survey.getId());
            dto.setResponseCount((int) responseCount);
        } catch (Exception e) {
            dto.setResponseCount(0);
        }
        
        return dto;
    }
}
