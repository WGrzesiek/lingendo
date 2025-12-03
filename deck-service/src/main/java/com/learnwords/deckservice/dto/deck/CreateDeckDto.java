package com.learnwords.deckservice.dto.deck;

import com.learnwords.deckservice.enums.*;
import jakarta.validation.constraints.NotBlank;
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
public class CreateDeckDto {

    @NotBlank(message = "Nazwa talii nie może być pusta")
    private String deckName;

    private String description;

    @NotNull(message = "Należy wybrać algorytm nauki")
    private LearnAlgorithm learnAlgorithm;

    @Builder.Default
    @NotNull(message = "Należy podać ilość słówek do nauki w jednej sesji")
    @Positive(message = "Ilość słówek musi być większa od zera")
    private Long howManyFlashcardsForOneSession = 20L;

    @NotNull(message = "Należy wybrać język źródłowy")
    private Language languageFrom;

    @NotNull(message = "Należy wybrać język docelowy")
    private Language languageTo;

    @NotNull(message = "Należy określić właściciela talii")
    private DeckOwner owner;

    @NotNull(message = "Należy określić kategorię talii")
    private DeckCategory category;

    @NotNull(message = "Należy określić poziom trudności talii")
    private DeckDifficulty difficulty;

    @Builder.Default
    @NotNull(message = "Określ, czy talia ma być publiczna czy prywatna")
    DeckVisibility visibility = DeckVisibility.PRIVATE;
}
