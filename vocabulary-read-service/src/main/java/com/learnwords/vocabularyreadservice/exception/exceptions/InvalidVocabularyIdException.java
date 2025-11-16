package com.learnwords.vocabularyreadservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy ID słownictwa jest nieprawidłowe (null lub puste).
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidVocabularyIdException extends RuntimeException {
    
    public InvalidVocabularyIdException() {
        super("ID słownictwa nie może być puste");
    }
    
    public InvalidVocabularyIdException(String message) {
        super(message);
    }
}
