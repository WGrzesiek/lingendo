package com.learnwords.deckservice.dto.dashboard;

import com.learnwords.deckservice.enums.DeckCategory;
import com.learnwords.deckservice.enums.DeckDifficulty;
import com.learnwords.deckservice.enums.DeckOwner;

import java.time.Instant;

public record StudentMyCourseListItemDto(String deckId,
                                         String deckName,
                                         String deckDescription,
                                         Long totalSession,
                                         Long learnedSession,
                                         Integer progressPercentage,
                                         Instant lastAccessed,
                                         DeckDifficulty deckDifficulty,
                                         DeckOwner deckOwner,
                                         DeckCategory deckCategory
                                         ) {
}
