package com.learnwords.statisticsservice.dto.leaderboard;

import java.util.List;

public record LeaderboardOverviewDto(
        List<LeaderboardEntryDto> top3,
        LeaderboardEntryDto you,
        LeaderboardEntryDto aboveYou
) {}
