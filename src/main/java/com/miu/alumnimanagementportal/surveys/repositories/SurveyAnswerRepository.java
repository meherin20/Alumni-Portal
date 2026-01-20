package com.miu.alumnimanagementportal.surveys.repositories;

import com.miu.alumnimanagementportal.surveys.entities.SurveyAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyAnswerRepository extends JpaRepository<SurveyAnswer, Long> {
}
