package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.SendWordFromKafkaDto;

/**
 * Serwis aktualizujący projekcję odczytu słownictwa.
 * 
 * <p>Odpowiada za przetwarzanie eventów Kafka związanych z operacjami na słownictwie
 * i aktualizację projekcji odczytu w bazie danych Read Service.
 */
public interface VocabularyProjectionUpdater {
    void processVocabularyCreate(SendWordFromKafkaDto sendWordFromKafkaDto);
}
