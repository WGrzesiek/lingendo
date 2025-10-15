package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.VocabularyDto;

public interface VocabularyProjectionUpdater {
    void processSentenceCreate(VocabularyDto vocabularyDto);
}
