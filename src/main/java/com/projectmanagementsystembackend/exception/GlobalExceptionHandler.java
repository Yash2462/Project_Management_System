package com.projectmanagementsystembackend.exception;

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
}
