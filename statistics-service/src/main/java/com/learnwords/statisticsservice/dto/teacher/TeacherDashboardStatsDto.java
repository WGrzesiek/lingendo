package com.learnwords.statisticsservice.dto.teacher;

/**
 * DTO ze statystykami dashboardu nauczyciela.
 */
public record TeacherDashboardStatsDto(
        int totalStudents,
        int activeStudents,
        int sharedDecks,
        long completedLessons
) {}
