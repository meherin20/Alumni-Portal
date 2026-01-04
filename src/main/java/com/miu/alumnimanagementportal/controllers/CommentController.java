package com.miu.alumnimanagementportal.controllers;

import com.miu.alumnimanagementportal.common.Converter;
import com.miu.alumnimanagementportal.dtos.CommentDto;
import com.miu.alumnimanagementportal.services.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/comments")
public class CommentController {

    private final CommentService commentService;
    private final Converter converter;

    @PostMapping
    public ResponseEntity<?> createComment(@RequestBody Map<String, Object> commentData) {
        try {
            CommentDto commentDto = new CommentDto();
            commentDto.setContent((String) commentData.get("content"));
            commentDto.setCategory((String) commentData.get("category"));
            commentDto.setSource((String) commentData.get("source"));
            commentDto.setUserEmail((String) commentData.get("userEmail"));
            commentDto.setIsRead(false);

            CommentDto savedComment = commentService.save(commentDto);

            return converter.buildResponseEntity(Map.of(
                "success", true,
                "message", "Comment submitted successfully",
                "data", savedComment
            ), HttpStatus.CREATED);

        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "message", "Failed to submit comment: " + e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllComments() {
        try {
            List<CommentDto> comments = commentService.findAll();
            return converter.buildResponseEntity(Map.of(
                "success", true,
                "data", comments,
                "count", comments.size()
            ), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "data", List.of(),
                "message", "Failed to load comments: " + e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/unread")
    public ResponseEntity<?> getUnreadComments() {
        try {
            List<CommentDto> comments = commentService.findUnread();
            return converter.buildResponseEntity(Map.of(
                "success", true,
                "data", comments,
                "count", comments.size()
            ), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "data", List.of()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getCommentStats() {
        try {
            long total = commentService.findAll().size();
            long unread = commentService.countUnread();
            long today = commentService.countToday();

            return converter.buildResponseEntity(Map.of(
                "success", true,
                "stats", Map.of(
                    "total", total,
                    "unread", unread,
                    "today", today
                )
            ), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "stats", Map.of("total", 0, "unread", 0, "today", 0)
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/reply")
    public ResponseEntity<?> replyToComment(@PathVariable Long id, @RequestBody Map<String, String> replyData) {
        try {
            String reply = replyData.get("reply");
            if (reply == null || reply.trim().isEmpty()) {
                return converter.buildResponseEntity(Map.of(
                    "success", false,
                    "message", "Reply cannot be empty"
                ), HttpStatus.BAD_REQUEST);
            }

            CommentDto updatedComment = commentService.updateReply(id, reply.trim());

            return converter.buildResponseEntity(Map.of(
                "success", true,
                "message", "Reply sent successfully",
                "data", updatedComment
            ), HttpStatus.OK);

        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "message", "Failed to send reply: " + e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        try {
            commentService.markAsRead(id);
            return converter.buildResponseEntity(Map.of(
                "success", true,
                "message", "Comment marked as read"
            ), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "message", "Failed to mark as read: " + e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable Long id) {
        try {
            commentService.deleteById(id);
            return converter.buildResponseEntity(Map.of(
                "success", true,
                "message", "Comment deleted successfully"
            ), HttpStatus.OK);
        } catch (Exception e) {
            return converter.buildResponseEntity(Map.of(
                "success", false,
                "message", "Failed to delete comment: " + e.getMessage()
            ), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
