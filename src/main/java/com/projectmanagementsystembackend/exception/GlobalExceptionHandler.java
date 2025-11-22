package com.projectmanagementsystembackend.exception;

import com.projectmanagementsystembackend.vo.ResponseMessage;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ResponseMessage message = new ResponseMessage();
        message.setMessage("Validation failed");
        message.setStatus(400);
        message.setData(errors);

        log.warn("Validation error: {}", errors);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getConstraintViolations().forEach(violation -> {
            String propertyPath = violation.getPropertyPath().toString();
            String message = violation.getMessage();
            errors.put(propertyPath, message);
        });

        ResponseMessage message = new ResponseMessage();
        message.setMessage("Constraint violation");
        message.setStatus(400);
        message.setData(errors);

        log.warn("Constraint violation: {}", errors);
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimitExceeded(RateLimitExceededException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(429);

        log.warn("Rate limit exceeded: {}", ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ChatNotFound.class)
    public ResponseEntity<Object> handleChatNotFound(ChatNotFound ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(404);

        log.warn("Chat not found: {}", ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Object> handleProjectNotFound(ProjectNotFoundException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(404);

        log.warn("Project not found: {}", ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleUserNotFound(UserNotFoundException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(404);

        log.warn("User not found: {}", ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(400);

        log.warn("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception ex, WebRequest request) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage("An unexpected error occurred");
        message.setStatus(500);

        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return new ResponseEntity<>(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
