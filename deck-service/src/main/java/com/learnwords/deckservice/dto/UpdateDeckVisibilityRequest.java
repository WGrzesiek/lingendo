package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.enums.DeckVisibility;
import jakarta.validation.constraints.NotNull;

public record UpdateDeckVisibilityRequest(
    @NotNull(message = "Należy określić widoczność talii")
    DeckVisibility deckVisibility
) {
}
