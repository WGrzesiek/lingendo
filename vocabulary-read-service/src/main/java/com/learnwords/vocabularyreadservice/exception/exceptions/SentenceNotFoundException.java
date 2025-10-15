package com.learnwords.vocabularyreadservice.exception.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class SentenceNotFoundException extends RuntimeException {
    public SentenceNotFoundException(String sentenceId) {
        super("Sentence with ID '" + sentenceId + "' not found");
    }
}
