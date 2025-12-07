package com.learnwords.common.events;

import java.time.Instant;

public record DeckEnrollmentsCreated (
        Instant eventTime,
        String deckEnrollmentId,
        String deckId,
        String userId,
        Instant receivedAt
) implements DomainEvent{
}
