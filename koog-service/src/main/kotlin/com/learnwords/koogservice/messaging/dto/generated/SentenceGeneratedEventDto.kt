package com.learnwords.koogservice.messaging.dto.generated

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

/**
 * DTO eventu wygenerowanych zdań wysyłanego przez Kafka.
 * 
 * @param eventId unikalny ID eventu
 * @param correlationId ID korelacji z oryginalnym żądaniem
 * @param jobId ID zadania generowania
 * @param wordId ID słówka, dla którego wygenerowano zdania
 * @param sentences lista wygenerowanych zdań
 * @param metadata metadane generowania
 * @param generatedAt timestamp wygenerowania
 */
data class SentenceGeneratedEventDto(
    @JsonProperty("event_id")
    val eventId: String,

    @JsonProperty("correlation_id")
    val correlationId: String,

    @JsonProperty("job_id")
    val jobId: String?,

    @JsonProperty("word_id")
    val wordId: String,

    @JsonProperty("sentences")
    val sentences: List<GeneratedSentenceDto>,

    @JsonProperty("metadata")
    val metadata: GenerationMetadataDto,

    @JsonProperty("generated_at")
    val generatedAt: Instant = Instant.now()
)




