package com.learnwords.koogservice.messaging.dto

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO pojedynczego słówka do generowania zdań.
 */
data class WordItemDto(
    @JsonProperty("word_id")
    val wordId: String,

    @JsonProperty("word")
    val word: String,

    @JsonProperty("translations")
    val translations: List<String>,
)
