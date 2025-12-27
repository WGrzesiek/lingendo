package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record GroupMemberRemovedEvent(
        Instant eventTime,
        String groupId,
        String teacherId,
        String studentId,
        String reason
) implements DomainEvent {}
