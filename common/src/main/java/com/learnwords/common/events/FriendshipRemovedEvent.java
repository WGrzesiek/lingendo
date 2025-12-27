package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FriendshipRemovedEvent(
        Instant eventTime,
        String userId1,
        String userId2,
        String reason  // REMOVED, BLOCKED
) implements DomainEvent {}
