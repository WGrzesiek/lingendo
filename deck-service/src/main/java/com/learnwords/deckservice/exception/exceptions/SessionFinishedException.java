package com.learnwords.deckservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class SessionFinishedException extends RuntimeException {
    public SessionFinishedException(String message) {
        super(message);
    }
}
