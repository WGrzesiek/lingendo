package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FlashcardCreatedEvent(
        Instant eventTime,
        String flashcardId,
        String deckId,
        String userId,
        Instant receivedAt
) implements DomainEvent {}
