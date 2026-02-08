package com.learnwords.statisticsservice.dto.teacher;

import java.time.Instant;

/**
 * DTO reprezentujące kurs nauczyciela.
 */
public record TeacherCourseDto(
        String deckId,
        String deckName,
        int studentsCount,
        Instant lastActivity
) {}
