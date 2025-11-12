package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import jakarta.validation.constraints.NotNull;

public record UpdateLearnAlgorithmRequest(
    @NotNull(message = "Należy wybrać algorytm nauki")
    LearnAlgorithm learnAlgorithm
) {
}
