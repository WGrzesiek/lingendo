package com.learnwords.statisticsservice.dto.course;

import java.util.List;

public record DeckStatsRequest(
        List<String> deckIds
) {}

