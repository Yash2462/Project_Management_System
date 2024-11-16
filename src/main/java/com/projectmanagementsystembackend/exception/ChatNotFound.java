package com.projectmanagementsystembackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ChatNotFound extends RuntimeException{
    public ChatNotFound(String message) {
        super(message);
    }
}
