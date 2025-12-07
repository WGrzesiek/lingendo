package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeckEnrollmentsCreated (
        Instant eventTime,
        String deckEnrollmentId,
        String deckId,
        String userId,
        Instant receivedAt
) implements DomainEvent{
}
