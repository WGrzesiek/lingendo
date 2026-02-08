package com.learnwords.statisticsservice.dto.friendship;

/**
 * DTO reprezentujący pozycję znajomego w rankingu.
 */
public record FriendLeaderboardEntryDto(
        int rank,
        String friendId,
        String friendName,
        long totalPoints,
        int sessions,
        double accuracy
) {}
