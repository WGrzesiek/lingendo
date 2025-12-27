package com.learnwords.statisticsservice.dto.group;

import java.time.Instant;

/**
 * DTO elementu aktywności grupy.
 */
public record GroupActivityItemDto(
        Instant eventTime,
        String studentId,
        String studentName,
        String activityType,
        String deckId,
        String deckName
) {}
