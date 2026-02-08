package com.learnwords.common.dto;

import com.learnwords.common.EventStatus;

public record UpdateOutboxEventDto(
        String aggregateId, EventStatus eventStatus
) {
}
