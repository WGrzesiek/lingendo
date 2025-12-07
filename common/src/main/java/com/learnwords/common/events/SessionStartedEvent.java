package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record SessionStartedEvent (
    Instant eventTime,
    String sessionId,
    String userId,
    String deckId,
    String deckEnrollmentId,
    Instant receivedAt
) implements DomainEvent {}