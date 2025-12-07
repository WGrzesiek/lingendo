package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserLoginEvent(
        Instant eventTime,
        String userId,
        String username,
        Instant received_at
) implements DomainEvent {}
