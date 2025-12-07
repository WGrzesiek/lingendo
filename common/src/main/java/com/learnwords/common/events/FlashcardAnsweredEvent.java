package com.learnwords.common.events;

import java.time.Instant;

public record FlashcardAnsweredEvent(
        Instant eventTime,
        String userId,
        String deckEnrollmentId,
        String sessionId,
        String flashcardId,
        boolean correct,
        Instant receivedAt
) implements DomainEvent {}

