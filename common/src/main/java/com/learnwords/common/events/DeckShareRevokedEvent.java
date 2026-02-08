package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeckShareRevokedEvent(
        Instant eventTime,
        String deckId,
        String ownerId,
        String targetType,
        String targetId,
        Instant revokedAt,
        Instant receivedAt
) implements DomainEvent {
}
