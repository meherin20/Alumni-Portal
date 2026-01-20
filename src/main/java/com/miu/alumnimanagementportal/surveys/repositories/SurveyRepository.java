package com.miu.alumnimanagementportal.surveys.repositories;

import com.miu.alumnimanagementportal.surveys.entities.Survey;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {

    @EntityGraph(attributePaths = {"questions"})
    @Query("select distinct s from Survey s where s.id = :id")
    Optional<Survey> findByIdWithQuestions(Long id);

    @EntityGraph(attributePaths = {"questions"})
    @Query("select distinct s from Survey s")
    List<Survey> findAllWithQuestions();

    @EntityGraph(attributePaths = {"questions"})
    @Query("select distinct s from Survey s where s.published = true")
    List<Survey> findPublishedWithQuestions();
}
