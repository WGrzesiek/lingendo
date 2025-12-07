package com.learnwords.common.events;

import java.time.Instant;

public record DeckEnrollmentsFinished(
        Instant eventTime,
        String deckEnrollmentId,
        String deckId,
        String userId,
        Integer correctAnswers,
        Integer incorrectAnswers,
        Instant receivedAt

) implements DomainEvent{
}
