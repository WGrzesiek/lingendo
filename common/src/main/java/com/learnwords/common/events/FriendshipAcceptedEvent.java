package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record FriendshipAcceptedEvent(
        Instant eventTime,
        String userId1,
        String userId2
) implements DomainEvent {}
