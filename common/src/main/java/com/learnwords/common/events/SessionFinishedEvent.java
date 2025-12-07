package com.learnwords.common.events;

import java.time.Instant;

public record SessionFinishedEvent(
        Instant eventTime,
        Instant startedAt,
        String sessionId,
        String userId,
        String deckId,
        String deckEnrollmentId,
        int correctAnswers,
        int incorrectAnswers,
        Instant receivedAt
) implements DomainEvent {}
