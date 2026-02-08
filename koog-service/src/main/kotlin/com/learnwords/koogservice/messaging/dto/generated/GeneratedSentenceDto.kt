package com.learnwords.koogservice.messaging.dto.generated

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO pojedynczego wygenerowanego zdania.
 */
data class GeneratedSentenceDto(
    @JsonProperty("sentence")
    val sentence: String,

    @JsonProperty("translation")
    val translation: String
)