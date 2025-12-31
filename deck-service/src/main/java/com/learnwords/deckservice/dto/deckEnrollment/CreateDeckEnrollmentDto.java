package com.learnwords.deckservice.dto.deckEnrollment;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO do tworzenia zapisu na talię.
 * Wszystkie pola są opcjonalne - jeśli nie podano, backend użyje wartości domyślnych z talii.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeckEnrollmentDto {
    private LearnAlgorithm preferredAlgorithm;
    @Positive(message = "Ilość słówek musi być większa od zera")
    private Long howManyFlashcardsForOneSession;
}
