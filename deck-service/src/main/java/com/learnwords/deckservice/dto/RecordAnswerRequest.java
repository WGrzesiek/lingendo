package com.learnwords.deckservice.dto;

public record RecordAnswerRequest(
        String flashcardId,
        boolean isCorrect
) {}
