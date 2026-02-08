package com.learnwords.statisticsservice.dto;

public record StudentDashboardStatsDto(
        int activeDecks,
        int completedLessonsThisMonth,
        int streakDays,
        long totalPoints,
        long pointsThisWeek
) {}

