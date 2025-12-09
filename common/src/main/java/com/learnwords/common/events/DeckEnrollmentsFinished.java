package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeckEnrollmentsFinished(
        Instant eventTime,
        String deckEnrollmentId,
        String deckId,
        String deckName,
        String userId,
        Instant receivedAt

) implements DomainEvent{
}
