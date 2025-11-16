package com.learnwords.vocabularyreadservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Wyjątek rzucany gdy słownictwo o podanym ID nie zostało znalezione.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class VocabularyNotFoundException extends RuntimeException {
    
    public VocabularyNotFoundException(String vocabularyId) {
        super("Słownictwo o ID '" + vocabularyId + "' nie zostało znalezione");
    }
}
