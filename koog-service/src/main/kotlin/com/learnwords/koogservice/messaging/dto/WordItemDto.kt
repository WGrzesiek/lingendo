package com.learnwords.koogservice.messaging.dto

/**
 * DTO pojedynczego słówka do generowania zdań.
 */
data class WordItemDto(
    val wordId: String,
    val word: String,
    val translations: List<String>,
)
