package com.learnwords.koogservice.messaging.dto

/**
 * DTO żądania generowania zdań z Kafki.
 *
 * @param requestedByUserId ID użytkownika, który zlecił generowanie
 * @param words lista słówek do wygenerowania zdań
 * @param level poziom językowy (A1, A2, B1, B2, C1, C2)
 * @param category kategoria tematyczna
 */
data class SentenceGenerationRequestDto(
    val id: String,
    val requestedByUserId: String,
    val words: List<WordItemDto>,
    val level: String = "B1",
    val category: String = "general",
    val languageFrom: String,
    val languageTo: String,
)


