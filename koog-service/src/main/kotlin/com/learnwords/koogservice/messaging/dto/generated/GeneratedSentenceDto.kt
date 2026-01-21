package com.learnwords.koogservice.messaging.dto.generated

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO pojedynczego wygenerowanego zdania.
 */
data class GeneratedSentenceDto(
    val sentence: String,
    val translation: String
)