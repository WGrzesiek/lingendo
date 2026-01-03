package com.learnwords.statisticsservice.dto.friendship;

/**
 * DTO znajomego z punktami i rankingiem.
 *
 */
public record FriendEnrichedDto(
        String friendId,
        String username,
        long totalPoints,
        long weeklyPoints,
        int globalRank
) {}
