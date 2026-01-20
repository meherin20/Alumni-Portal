package com.miu.alumnimanagementportal.surveys.services.impl;

import com.miu.alumnimanagementportal.entities.User;
import com.miu.alumnimanagementportal.exceptions.BadRequestException;
import com.miu.alumnimanagementportal.exceptions.ResourceNotFoundException;
import com.miu.alumnimanagementportal.repositories.UserRepository;
import com.miu.alumnimanagementportal.surveys.dtos.SurveyAnswerRequest;
import com.miu.alumnimanagementportal.surveys.dtos.SurveySubmitRequest;
import com.miu.alumnimanagementportal.surveys.entities.Survey;
import com.miu.alumnimanagementportal.surveys.entities.SurveyAnswer;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestion;
import com.miu.alumnimanagementportal.surveys.entities.SurveyQuestionOption;
import com.miu.alumnimanagementportal.surveys.entities.SurveyResponse;
import com.miu.alumnimanagementportal.surveys.enums.SurveyQuestionType;
import com.miu.alumnimanagementportal.surveys.repositories.SurveyRepository;
import com.miu.alumnimanagementportal.surveys.repositories.SurveyResponseRepository;
import com.miu.alumnimanagementportal.surveys.services.SurveyResponseService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SurveyResponseServiceImpl implements SurveyResponseService {

    private final SurveyRepository surveyRepository;
    private final SurveyResponseRepository responseRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void submit(Long surveyId, SurveySubmitRequest request) {
        Survey survey = surveyRepository.findByIdWithQuestions(surveyId)
                .orElseThrow(() -> new ResourceNotFoundException("Survey not found"));
        if (!survey.isPublished()) {
            throw new BadRequestException("Survey is not published");
        }

        User user = userRepository.findByEmail(request.getUserEmail());
        if (user == null) {
            throw new BadRequestException("User not found");
        }
        if (responseRepository.existsBySurveyIdAndUserId(surveyId, user.getId())) {
            throw new BadRequestException("You already submitted this survey");
        }

        Map<Long, SurveyQuestion> questionById = new HashMap<>();
        for (SurveyQuestion q : survey.getQuestions()) {
            questionById.put(q.getId(), q);
        }

        Map<Long, SurveyAnswerRequest> answersByQuestion = new HashMap<>();
        for (SurveyAnswerRequest answer : request.getAnswers()) {
            answersByQuestion.put(answer.getQuestionId(), answer);
        }

        for (SurveyQuestion q : survey.getQuestions()) {
            if (q.isRequired() && !answersByQuestion.containsKey(q.getId())) {
                throw new BadRequestException("Missing required answer for: " + q.getQuestionText());
            }
        }

        SurveyResponse response = new SurveyResponse();
        response.setSurvey(survey);
        response.setUser(user);
        response.setStartedAt(LocalDateTime.now());
        response.setSubmittedAt(LocalDateTime.now());

        for (SurveyAnswerRequest answerRequest : request.getAnswers()) {
            SurveyQuestion question = questionById.get(answerRequest.getQuestionId());
            if (question == null) {
                throw new BadRequestException("Invalid question in response");
            }
            String normalized = normalizeAnswer(question, answerRequest.getAnswerValue());
            SurveyAnswer answer = new SurveyAnswer();
            answer.setResponse(response);
            answer.setQuestion(question);
            answer.setAnswerValue(normalized);
            response.getAnswers().add(answer);
        }

        responseRepository.save(response);
    }

    private String normalizeAnswer(SurveyQuestion question, String rawValue) {
        if (rawValue == null || rawValue.trim().isEmpty()) {
            if (question.isRequired()) {
                throw new BadRequestException("Answer is required for: " + question.getQuestionText());
            }
            return "";
        }

        String value = rawValue.trim();
        SurveyQuestionType type = question.getQuestionType();

        if (type == SurveyQuestionType.MCQ) {
            ensureOptionExists(question, value);
            return value;
        }
        if (type == SurveyQuestionType.CHECKBOX) {
            List<String> tokens = Arrays.stream(value.split(","))
                    .map(String::trim)
                    .filter(v -> !v.isEmpty())
                    .toList();
            if (tokens.isEmpty() && question.isRequired()) {
                throw new BadRequestException("Answer is required for: " + question.getQuestionText());
            }
            for (String token : tokens) {
                ensureOptionExists(question, token);
            }
            return String.join(", ", tokens);
        }
        if (type == SurveyQuestionType.RATING) {
            try {
                int rating = Integer.parseInt(value);
                if (rating < 1 || rating > 5) {
                    throw new BadRequestException("Rating must be between 1 and 5");
                }
                return String.valueOf(rating);
            } catch (NumberFormatException ex) {
                throw new BadRequestException("Rating must be a number between 1 and 5");
            }
        }
        return value;
    }

    private void ensureOptionExists(SurveyQuestion question, String value) {
        boolean exists = question.getOptions()
                .stream()
                .map(SurveyQuestionOption::getLabel)
                .anyMatch(opt -> opt.equalsIgnoreCase(value));
        if (!exists) {
            throw new BadRequestException("Invalid option for question: " + question.getQuestionText());
        }
    }
}
