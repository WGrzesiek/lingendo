package com.learnwords.statisticsservice.dto.leaderboard;

public record LeaderboardEntryDto(
        String userId,
        int rank,
        String displayName,
        long points,
        int completedCourses
) {}