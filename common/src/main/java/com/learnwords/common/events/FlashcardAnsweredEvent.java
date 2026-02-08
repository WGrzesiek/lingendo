package com.learnwords.common.events;

import lombok.Builder;

import java.time.Duration;
import java.time.Instant;

@Builder
public record FlashcardAnsweredEvent(
        Instant eventTime,
        String userId,
        String deckEnrollmentId,
        String sessionId,
        String flashcardId,
        boolean correct,
        Instant receivedAt,
        Duration timeTaken
) implements DomainEvent {}

