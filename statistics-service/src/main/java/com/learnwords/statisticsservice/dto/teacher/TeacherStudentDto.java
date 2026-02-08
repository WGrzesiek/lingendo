package com.learnwords.statisticsservice.dto.teacher;

import java.time.Instant;

/**
 * DTO reprezentujące ucznia nauczyciela.
 */
public record TeacherStudentDto(
        String studentId,
        String studentName,
        long totalPoints,
        Instant lastActive
) {}
