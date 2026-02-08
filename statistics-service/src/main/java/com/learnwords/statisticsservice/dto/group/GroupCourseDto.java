package com.learnwords.statisticsservice.dto.group;

import java.time.Instant;

/**
 * DTO kursu udostępnionego grupie.
 */
public record GroupCourseDto(
        String deckId,
        String deckName,
        int studentsCount,
        Instant lastActivity
) {}
