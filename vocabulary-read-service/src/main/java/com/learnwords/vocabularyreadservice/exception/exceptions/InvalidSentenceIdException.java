package com.learnwords.vocabularyreadservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy ID zdania jest nieprawidłowe (null lub puste).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidSentenceIdException extends RuntimeException {
    
    public InvalidSentenceIdException() {
        super("ID zdania nie może być puste");
    }
    
    public InvalidSentenceIdException(String message) {
        super(message);
    }
}
