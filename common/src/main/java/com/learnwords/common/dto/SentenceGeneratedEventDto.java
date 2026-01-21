package com.learnwords.common.dto;

import java.time.Instant;
import java.util.List;

/**
 * DTO eventu wygenerowanych zdań AI z koog-service.
 */
public record SentenceGeneratedEventDto(
    String eventId,
    String correlationId,
    String jobId,
    String wordId,
    List<GeneratedSentenceDto> sentences,
    GenerationMetadataDto metadata,
    Instant generatedAt
) {
    
    /**
     * DTO pojedynczego wygenerowanego zdania.
     */
    public record GeneratedSentenceDto(
        String sentence,
        String translation
    ) {}
    
    /**
     * Metadane procesu generowania.
     */
    public record GenerationMetadataDto(
        String model,
        int promptVersion,
        String level,
        String category,
        String languageFrom,
        String languageTo,
        Double costEstimate
    ) {}
}
