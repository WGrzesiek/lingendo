package com.learnwords.statisticsservice.dto.group;

/**
 * DTO pozycji w rankingu grupy.
 */
public record GroupLeaderboardEntryDto(
        int rank,
        String studentId,
        String studentName,
        long correctAnswers,
        int sessions,
        double accuracy
) {}
