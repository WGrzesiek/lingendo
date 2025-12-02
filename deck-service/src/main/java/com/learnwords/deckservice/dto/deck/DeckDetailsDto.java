package com.learnwords.deckservice.dto.deck;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO dla szczegółów talii - używane do:
 * 1. Pobrania pełnych informacji o talii (GET)
 * 2. Edycji talii (PUT/PATCH)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckDetailsDto {

    private String id;
    private String ownerId;
    private int wordCount;


    @NotBlank(message = "Nazwa talii nie może być pusta")
    private String name;
    private String description;
    @NotNull(message = "Należy określić widoczność talii")
    private DeckVisibility visibility;
    @NotNull(message = "Należy wybrać właściciela talii")
    private DeckOwner owner;
    @NotNull(message = "Należy wybrać algorytm nauki")
    private LearnAlgorithm learnAlgorithm;
    @NotNull(message = "Należy podać ilość fiszek na sesję")
    @Positive(message = "Ilość fiszek musi być większa od zera")
    private Long howManyFlashcardsForOneSession;
    @NotNull(message = "Należy wybrać język źródłowy")
    private Language languageFrom;
    @NotNull(message = "Należy wybrać język docelowy")
    private Language languageTo;

    @NotNull(message = "Talia musi posiadać kategorię")
    private DeckCategory category;

    @NotNull(message = "Talia musi posiadać poziom trudności")
    private DeckDifficulty difficulty;

    /**
     * Tworzy DTO z encji Deck (do GET)
     */
    public static DeckDetailsDto from(Deck deck) {
        return DeckDetailsDto.builder()
                .id(deck.getId())
                .ownerId(deck.getOwnerId())
                .name(deck.getName())
                .description(deck.getDescription())
                .visibility(deck.getVisibility())
                .owner(deck.getOwner())
                .wordCount(deck.getWordCount())
                .learnAlgorithm(deck.getLearnAlgorithm())
                .howManyFlashcardsForOneSession(deck.getHowManyFlashcardsForOneSession())
                .languageFrom(deck.getLanguageFrom())
                .languageTo(deck.getLanguageTo())
                .category(deck.getCategory())
                .difficulty(deck.getDifficulty())
                .build();
    }
    
    /**
     * Aktualizuje encję Deck z DTO (do PUT/PATCH)
     * Uwaga: NIE aktualizuje pól tylko do odczytu (id, userId, wordCount, timestamps)
     */
    public void updateEntity(Deck deck) {
        deck.setName(this.name);
        deck.setDescription(this.description);
        deck.setVisibility(this.visibility);
        deck.setOwner(this.owner);
        deck.setLearnAlgorithm(this.learnAlgorithm);
        deck.setHowManyFlashcardsForOneSession(this.howManyFlashcardsForOneSession);
        deck.setLanguageFrom(this.languageFrom);
        deck.setLanguageTo(this.languageTo);
        deck.setCategory(this.category);
        deck.setDifficulty(this.difficulty);
    }
}
