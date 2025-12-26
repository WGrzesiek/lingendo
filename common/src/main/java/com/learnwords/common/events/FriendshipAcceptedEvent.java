package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FriendshipAcceptedEvent(
        Instant eventTime,
        String friendshipId,
        String userId1,
        String username1,
        String userId2,
        String username2,
        Instant receivedAt
) implements DomainEvent {}
