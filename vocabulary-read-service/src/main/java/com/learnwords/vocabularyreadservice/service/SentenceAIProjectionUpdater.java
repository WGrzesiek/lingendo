package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.SentenceGeneratedEventDto;

/**
 * Serwis aktualizujący projekcję odczytu zdań AI.
 */
public interface SentenceAIProjectionUpdater {

    void processSentenceAIGenerated(SentenceGeneratedEventDto event);
}
