package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record GroupMemberRemovedEvent(
        Instant eventTime,
        String groupId,
        String groupName,
        String teacherId,
        String studentId,
        String reason,
        Instant receivedAt
) implements DomainEvent {}
