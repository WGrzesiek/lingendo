package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeckSharedEvent(
        Instant eventTime,
        String deckId,
        String deckName,
        String ownerId,
        String targetType,
        String targetId,
        Instant sharedAt,
        Instant receivedAt
) implements DomainEvent {
}
