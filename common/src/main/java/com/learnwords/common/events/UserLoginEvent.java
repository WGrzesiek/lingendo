package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record UserLoginEvent(
        String eventId,
        String userId,
        String username,
        String email,
        Instant occurredAt
) {}
