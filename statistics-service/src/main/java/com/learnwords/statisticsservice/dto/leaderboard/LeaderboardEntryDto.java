package com.learnwords.statisticsservice.dto.leaderboard;

import lombok.Builder;

@Builder
public record LeaderboardEntryDto(
        String userId,
        int rank,
        String displayName,
        long points,
        int completedCourses
) {}