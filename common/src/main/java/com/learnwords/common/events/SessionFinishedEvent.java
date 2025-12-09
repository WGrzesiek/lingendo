package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SessionFinishedEvent(
        Instant eventTime,
        Instant startedAt,
        String sessionId,
        String userId,
        String deckId,
        String deckName,
        String deckEnrollmentId,
        int correctAnswers,
        int incorrectAnswers,
        Instant receivedAt
) implements DomainEvent {}
