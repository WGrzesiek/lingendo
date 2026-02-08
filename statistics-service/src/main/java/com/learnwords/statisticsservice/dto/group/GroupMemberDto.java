package com.learnwords.statisticsservice.dto.group;

import java.time.Instant;

/**
 * DTO członka grupy.
 */
public record GroupMemberDto(
        String studentId,
        String studentName,
        long totalPoints,
        Instant lastActive
) {}
