package com.learnwords.deckservice.dto;

import com.learnwords.deckservice.entity.Deck;
import com.learnwords.deckservice.enums.DeckOwner;
import com.learnwords.deckservice.enums.Language;
import com.learnwords.deckservice.enums.LearnAlgorithm;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

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

    // === Pola tylko do odczytu (nie edytowalne) ===
    private String id;
    private String userId;
    private int wordCount;
    private Instant createdAt;
    private Instant updatedAt;

    // === Pola edytowalne ===
    @NotBlank(message = "Nazwa talii nie może być pusta")
    private String name;
    private String description;
    @NotNull(message = "Należy określić widoczność talii")
    private Boolean isPublic;
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

    /**
     * Tworzy DTO z encji Deck (do GET)
     */
    public static DeckDetailsDto from(Deck deck) {
        return DeckDetailsDto.builder()
                .id(deck.getId())
                .userId(deck.getUserId())
                .name(deck.getName())
                .description(deck.getDescription())
                .isPublic(deck.isPublic())
                .owner(deck.getOwner())
                .wordCount(deck.getWordCount())
                .learnAlgorithm(deck.getLearnAlgorithm())
                .howManyFlashcardsForOneSession(deck.getHowManyFlashcardsForOneSession())
                .languageFrom(deck.getLanguageFrom())
                .languageTo(deck.getLanguageTo())
                .createdAt(deck.getCreatedAt())
                .updatedAt(deck.getUpdatedAt())
                .build();
    }
    
    /**
     * Aktualizuje encję Deck z DTO (do PUT/PATCH)
     * Uwaga: NIE aktualizuje pól tylko do odczytu (id, userId, wordCount, timestamps)
     */
    public void updateEntity(Deck deck) {
        deck.setName(this.name);
        deck.setDescription(this.description);
        deck.setPublic(this.isPublic);
        deck.setOwner(this.owner);
        deck.setLearnAlgorithm(this.learnAlgorithm);
        deck.setHowManyFlashcardsForOneSession(this.howManyFlashcardsForOneSession);
        deck.setLanguageFrom(this.languageFrom);
        deck.setLanguageTo(this.languageTo);
    }
}
