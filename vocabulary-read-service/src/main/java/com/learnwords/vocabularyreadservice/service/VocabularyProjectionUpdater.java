package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.SendWordFromKafkaDto;

public interface VocabularyProjectionUpdater {
    void processVocabularyCreate(SendWordFromKafkaDto sendWordFromKafkaDto);
}
