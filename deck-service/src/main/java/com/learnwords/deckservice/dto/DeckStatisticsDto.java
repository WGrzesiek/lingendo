package com.learnwords.deckservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckStatisticsDto {
    private String deckId;
    private String deckName;
    private int totalFlashcards;
    private int learnedFlashcards;
    private int unlearnedFlashcards;
    private double progressPercentage;
    private int totalSessions;
    private int completedSessions;
}
