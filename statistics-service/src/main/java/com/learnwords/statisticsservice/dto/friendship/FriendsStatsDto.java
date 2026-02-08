package com.learnwords.statisticsservice.dto.friendship;

import java.util.List;

/**
 * DTO z pełnymi statystykami znajomych.
 */
public record FriendsStatsDto(
        int totalFriends,
        List<FriendLeaderboardEntryDto> leaderboard) {}
