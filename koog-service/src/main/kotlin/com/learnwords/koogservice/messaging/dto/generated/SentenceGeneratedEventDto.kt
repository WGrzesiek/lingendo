package com.learnwords.koogservice.messaging.dto.generated

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

    val eventId: String,
    val correlationId: String,
    val jobId: String?,
    val wordId: String,
    val sentences: List<GeneratedSentenceDto>,
    val metadata: GenerationMetadataDto,
    val generatedAt: Instant = Instant.now()
)




