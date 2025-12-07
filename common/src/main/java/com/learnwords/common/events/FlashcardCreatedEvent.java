package com.learnwords.common.events;

import java.time.Instant;

public record FlashcardCreatedEvent(
        String eventTime,
        String flashcardId,
        String deckId,
        String userId,
        Instant receivedAt
) implements DomainEvent {}
