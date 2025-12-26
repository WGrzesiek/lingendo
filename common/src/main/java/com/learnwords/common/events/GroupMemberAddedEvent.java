package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record GroupMemberAddedEvent(
        Instant eventTime,
        String groupId,
        String groupName,
        String teacherId,
        String studentId,
        Instant receivedAt
) implements DomainEvent {}
