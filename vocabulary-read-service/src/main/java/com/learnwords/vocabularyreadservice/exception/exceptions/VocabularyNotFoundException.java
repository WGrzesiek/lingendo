package com.learnwords.vocabularyreadservice.exception.exceptions;

public class VocabularyNotFoundException extends RuntimeException {
    public VocabularyNotFoundException(String vocabularyId)
    {
        super("Vocabulary with ID '" + vocabularyId + "' not found");
    }
}
