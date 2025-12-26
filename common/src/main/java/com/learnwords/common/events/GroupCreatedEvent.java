package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record GroupCreatedEvent(
        Instant eventTime,
        String groupId,
        String groupName,
        String teacherId,
        Instant receivedAt
) implements DomainEvent {}
