package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FriendshipRemovedEvent(
        Instant eventTime,
        String friendshipId,
        String userId1,
        String userId2,
        String reason,  // REMOVED, BLOCKED
        Instant receivedAt
) implements DomainEvent {}
