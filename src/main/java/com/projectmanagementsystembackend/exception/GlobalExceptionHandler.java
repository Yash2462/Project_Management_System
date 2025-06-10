package com.projectmanagementsystembackend.exception;

import com.projectmanagementsystembackend.model.Project;
import com.projectmanagementsystembackend.vo.ResponseMessage;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RateLimitExceededException.class)
    public ResponseEntity<Object> handleRateLimitExceeded(RateLimitExceededException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage("MAX_LIMIT_EXCEEDED");
        message.setStatus(429);
        return new ResponseEntity<>(message,HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(ChatNotFound.class)
    public ResponseEntity<Object> handleRateLimitExceeded(ChatNotFound ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(404);
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ProjectNotFoundException.class)
    public ResponseEntity<Object> handleRateLimitExceeded(ProjectNotFoundException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(404);
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Object> handleRateLimitExceeded(UserNotFoundException ex) {
        ResponseMessage message = new ResponseMessage();
        message.setMessage(ex.getMessage());
        message.setStatus(404);
        return new ResponseEntity<>(message,HttpStatus.NOT_FOUND);
    }


}
