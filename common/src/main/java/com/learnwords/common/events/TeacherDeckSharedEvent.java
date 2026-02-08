package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TeacherDeckSharedEvent(
        Instant eventTime,
        String teacherId,
        String deckId,
        String deckName,
        Instant receivedAt
) implements DomainEvent {}
