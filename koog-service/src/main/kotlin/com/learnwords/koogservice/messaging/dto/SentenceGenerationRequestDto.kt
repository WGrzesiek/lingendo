package com.learnwords.koogservice.messaging.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO żądania generowania zdań z Kafki.
 *
 * @param requestedByUserId ID użytkownika, który zlecił generowanie
 * @param words lista słówek do wygenerowania zdań
 * @param level poziom językowy (A1, A2, B1, B2, C1, C2)
 * @param category kategoria tematyczna
 */
data class SentenceGenerationRequestDto(
    @JsonProperty("id")
    val id: String,
    
    @JsonProperty("requested_by_user_id")
    val requestedByUserId: String,
    
    @JsonProperty("words")
    val words: List<WordItemDto>,
    
    @JsonProperty("level")
    val level: String = "B1",
    
    @JsonProperty("category")
    val category: String = "general",

    @JsonProperty("language_from")
    val languageFrom: String,

    @JsonProperty("language_to")
    val languageTo: String,
)


