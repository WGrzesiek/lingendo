package com.learnwords.deckservice.dto;

import jakarta.validation.constraints.NotNull;

public record UpdateDeckVisibilityRequest(
    @NotNull(message = "Należy określić widoczność talii")
    Boolean isPublic
) {
}
