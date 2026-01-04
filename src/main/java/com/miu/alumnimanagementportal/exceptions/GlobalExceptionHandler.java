package com.miu.alumnimanagementportal.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<Object> handleBadRequestException(BadRequestException exception) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", exception.getMessage()));
    }
    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleStudentNotFoundException(ResourceNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", exception.getMessage()));
    }
    @ExceptionHandler({DataAlreadyExistException.class})
    public ResponseEntity<Object> handleStudentAlreadyExistsException(DataAlreadyExistException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", exception.getMessage()));
    }
    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", exception.getMessage()));
    }
}