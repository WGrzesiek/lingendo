package com.learnwords.koogservice.messaging.dto.generated

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Metadane procesu generowania.
 */
data class GenerationMetadataDto(
    @JsonProperty("model")
    val model: String,

    @JsonProperty("prompt_version")
    val promptVersion: Int,

    @JsonProperty("level")
    val level: String,

    @JsonProperty("category")
    val category: String,

    @JsonProperty("language_from")
    val languageFrom: String,

    @JsonProperty("language_to")
    val languageTo: String,

    @JsonProperty("cost_estimate")
    val costEstimate: Double? = null
)
