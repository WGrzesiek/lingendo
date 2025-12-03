package com.learnwords.deckservice.dto.deckEnrollment;

import com.learnwords.deckservice.enums.LearnAlgorithm;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDeckEnrollmentDto {
    @NotNull(message = "Wybierz algorytm nauki lub zostaw wybrany przez twórcę talii")
    private LearnAlgorithm preferredAlgorithm;

    @Builder.Default
    @NotNull(message = "Wybierz ilość słówek do nauki w jednej sesji lub zostaw wybraną przez twórcę talii")
    @Positive(message = "Ilość słówek musi być większa od zera")
    private Long howManyFlashcardsForOneSession = 20L;
}
