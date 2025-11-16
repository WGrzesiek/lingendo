package com.learnwords.deckservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateDeckNameRequest(
    @NotBlank(message = "Nazwa talii nie może być pusta")
    @Size(min = 1, max = 100, message = "Nazwa talii musi mieć od 1 do 100 znaków")
    String deckName
) {
}
