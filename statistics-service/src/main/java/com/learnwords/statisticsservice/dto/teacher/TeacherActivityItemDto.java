package com.learnwords.statisticsservice.dto.teacher;

import java.time.Instant;

/**
 * DTO reprezentujące aktywność ucznia w feedzie nauczyciela.
 */
public record TeacherActivityItemDto(
        Instant eventTime,
        String studentId,
        String studentName,
        String activityType,
        String deckId,
        String deckName
) {}
