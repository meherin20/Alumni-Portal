package com.miu.alumnimanagementportal.services.impl;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.CommentDto;
import com.miu.alumnimanagementportal.entities.Comment;
import com.miu.alumnimanagementportal.repositories.CommentRepository;
import com.miu.alumnimanagementportal.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final Converter converter;

    @Override
    public List<CommentDto> findAll() {
        return commentRepository.findAll().stream()
                .map(comment -> converter.convert(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto findById(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        return converter.convert(comment, CommentDto.class);
    }

    @Override
    @Transactional
    public CommentDto save(CommentDto commentDto) {
        Comment comment = converter.convert(commentDto, Comment.class);
        Comment savedComment = commentRepository.save(comment);
        return converter.convert(savedComment, CommentDto.class);
    }

    @Override
    @Transactional
    public CommentDto updateReply(Long id, String reply) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        comment.setReply(reply);
        comment.setRepliedAt(LocalDateTime.now());
        comment.setIsRead(true);

        Comment updatedComment = commentRepository.save(comment);
        return converter.convert(updatedComment, CommentDto.class);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        comment.setIsRead(true);
        commentRepository.save(comment);
    }

    @Override
    public List<CommentDto> findUnread() {
        return commentRepository.findByIsReadFalse().stream()
                .map(comment -> converter.convert(comment, CommentDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public long countUnread() {
        return commentRepository.countByIsReadFalse();
    }

    @Override
    public long countToday() {
        return commentRepository.countByToday(LocalDateTime.now());
    }
}
