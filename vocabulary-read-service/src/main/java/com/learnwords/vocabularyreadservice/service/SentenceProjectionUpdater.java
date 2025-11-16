package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.SentenceDto;

/**
 * Serwis aktualizujący projekcję odczytu zdań.
 * 
 * <p>Odpowiada za przetwarzanie eventów Kafka związanych z operacjami na zdaniach
 * i aktualizację projekcji odczytu w bazie danych Read Service.
 */
public interface SentenceProjectionUpdater {
    void processSentenceCreate(SentenceDto sentenceDto);

    }
