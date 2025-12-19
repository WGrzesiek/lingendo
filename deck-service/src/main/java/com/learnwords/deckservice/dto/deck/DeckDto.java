package com.learnwords.deckservice.dto.deck;

import com.learnwords.deckservice.enums.DeckCategory;
import com.learnwords.deckservice.enums.DeckDifficulty;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.DeckVisibility;
import lombok.Builder;

@Builder
public record DeckDto(String id,
                      String name,
                      String deckDescription,
                      DeckDifficulty deckDifficulty,
                      DeckOwner deckOwner,
                      DeckCategory deckCategory,
                      String ownerId,
                      int wordCount,
                      DeckVisibility visibility) {
}
