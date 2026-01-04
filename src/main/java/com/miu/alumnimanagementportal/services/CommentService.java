package com.miu.alumnimanagementportal.services;

import com.miu.alumnimanagementportal.dtos.CommentDto;

import java.util.List;

public interface CommentService {
    List<CommentDto> findAll();
    CommentDto findById(Long id);
    CommentDto save(CommentDto commentDto);
    CommentDto updateReply(Long id, String reply);
    void deleteById(Long id);
    void markAsRead(Long id);
    List<CommentDto> findUnread();
    long countUnread();
    long countToday();
}
