package com.learnwords.statisticsservice.dto;

import lombok.Builder;

@Builder
public record LeaderboardEntryDto(
        String userId,
        String displayName,
        long pointsCurrent,
        long pointsDiff,
        int rankCurrent,
        int rankPrevious
) {}