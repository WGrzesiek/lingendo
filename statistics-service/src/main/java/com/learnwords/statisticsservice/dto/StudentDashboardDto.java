package com.learnwords.statisticsservice.dto;

public record StudentDashboardDto(
        int activeDecks,
        int completedLessonsThisMonth,
        int streakDays,
        long totalPoints,
        long pointsThisWeek
) {}

