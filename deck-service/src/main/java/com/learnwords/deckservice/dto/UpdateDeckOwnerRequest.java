package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.enums.DeckOwner;
import jakarta.validation.constraints.NotNull;

public record UpdateDeckOwnerRequest(
    @NotNull(message = "Należy wybrać właściciela talii")
    DeckOwner newOwner
) {
}
