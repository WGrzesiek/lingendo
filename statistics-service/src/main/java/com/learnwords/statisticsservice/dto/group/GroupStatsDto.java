package com.learnwords.statisticsservice.dto.group;

/**
 * DTO statystyk grupy.
 */
public record GroupStatsDto(
        int totalMembers,
        int activeMembers,
        int sharedDecks,
        long completedLessons,
        long totalPoints,
        long totalWordsLearned,
        long totalStudyTimeMinutes,
        long totalSessions,
        double averageAccuracy,
        double averageWordsPerDay
) {}
