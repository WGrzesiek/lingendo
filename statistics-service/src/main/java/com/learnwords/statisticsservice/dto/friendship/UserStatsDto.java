package com.learnwords.statisticsservice.dto.friendship;

/**
 * DTO ze statystykami użytkownika dla widoku szczegółów znajomego.
 *
 */
public record UserStatsDto(
        String userId,
        String username,
        long totalPoints,
        long weeklyPoints,
        int globalRank,
        int totalSessions,
        int streakDays,
        double accuracy,
        int totalCorrect,
        int totalAnswers,
        String lastActiveAt
) {}
