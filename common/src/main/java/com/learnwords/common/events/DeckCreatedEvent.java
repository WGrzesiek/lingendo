package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record DeckCreatedEvent(
        Instant eventTime,
        String deckId,
        String userId,
        String deckName,
        String deckCategory,
        String languageFrom,
        String languageTo,
        Instant receivedAt
) implements DomainEvent {}
