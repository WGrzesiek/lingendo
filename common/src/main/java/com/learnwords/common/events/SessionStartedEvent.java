package com.learnwords.common.events;

import java.time.Instant;

public record SessionStartedEvent (
    Instant eventTime,
    String sessionId,
    String userId,
    String deckId,
    String deckEnrollmentId,
    Instant receivedAt
) implements DomainEvent {}