package com.miu.alumnimanagementportal.repositories;

import com.miu.alumnimanagementportal.entities.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByIsReadFalse();

    List<Comment> findByCategory(String category);

    @Query("SELECT c FROM Comment c WHERE DATE(c.timestamp) = DATE(:date)")
    List<Comment> findByDate(LocalDateTime date);

    long countByIsReadFalse();

    @Query("SELECT COUNT(c) FROM Comment c WHERE DATE(c.timestamp) = DATE(:today)")
    long countByToday(LocalDateTime today);
}
