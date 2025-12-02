package com.learnwords.deckservice.dto.deck;

import com.learnwords.deckservice.enums.DeckVisibility;

public record DeckDto(String id,
                      String name,
                      String ownerId,
                      String ownerType,
                      int wordCount,
                      DeckVisibility visibility) {
}
