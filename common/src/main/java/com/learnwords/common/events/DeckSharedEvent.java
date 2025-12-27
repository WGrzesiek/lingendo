package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeckSharedEvent(
        Instant eventTime,
        String deckId,
        String deckName,
        String ownerId,
        String targetType,  // GROUP, USER
        String targetId
) implements DomainEvent {
}
