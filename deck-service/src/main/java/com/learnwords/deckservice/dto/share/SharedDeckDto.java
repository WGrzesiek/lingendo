package com.learnwords.deckservice.dto.share;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO z informacją o talii dostępnej dla użytkownika przez udostępnienie.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SharedDeckDto {

    private String deckId;
    private String deckName;
    private String description;
    private String ownerId;
    private String ownerName;
    private String sharedVia;
    private String sharedViaName;
    private String message;
    private int flashcardCount;
    private String languageFrom;
    private String languageTo;
    private String difficulty;
    private String category;
}
