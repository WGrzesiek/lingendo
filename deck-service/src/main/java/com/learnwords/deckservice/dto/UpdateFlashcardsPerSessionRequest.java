package com.learnwords.deckservice.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateFlashcardsPerSessionRequest(
    @NotNull(message = "Należy podać liczbę fiszek na sesję")
    @Min(value = 1, message = "Liczba fiszek musi być większa od 0")
    @Max(value = 100, message = "Liczba fiszek nie może przekraczać 100")
    Long flashcardsPerSession
) {
}
