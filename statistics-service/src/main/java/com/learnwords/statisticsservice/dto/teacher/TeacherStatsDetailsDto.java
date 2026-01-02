package com.learnwords.statisticsservice.dto.teacher;

import java.util.Map;

/**
 * DTO ze szczegółowymi statystykami nauczyciela.
 * Zawiera dane o utworzonych kursach, fiszkach, punktach studentów itp.
 */
public record TeacherStatsDetailsDto(
        int createdDecks,
        int createdFlashcards,
        long totalStudentPoints,
        long totalStudentSessions,
        double averageAccuracy,
        int activeStudents,
        int totalStudents,
        long totalCorrectAnswers,
        long totalAnswers,
        Map<String, Long> pointsPerMonth
) {}
