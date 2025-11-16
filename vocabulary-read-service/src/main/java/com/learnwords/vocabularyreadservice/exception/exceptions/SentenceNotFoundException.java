package com.learnwords.vocabularyreadservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy zdanie o podanym ID nie zostało znalezione.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class SentenceNotFoundException extends RuntimeException {
    
    public SentenceNotFoundException(String sentenceId) {
        super("Zdanie o ID '" + sentenceId + "' nie zostało znalezione");
    }
}
