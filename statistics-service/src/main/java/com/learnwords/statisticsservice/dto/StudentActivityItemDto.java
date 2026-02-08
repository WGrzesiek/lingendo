package com.learnwords.statisticsservice.dto;

import java.time.Instant;

public record StudentActivityItemDto(
        String type,
        String title,
        String subtitle,
        int points,
        Instant eventTime
) {}
