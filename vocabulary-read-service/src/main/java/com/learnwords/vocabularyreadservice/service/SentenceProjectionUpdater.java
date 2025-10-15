package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.SentenceDto;

public interface SentenceProjectionUpdater {
    void processSentenceCreate(SentenceDto sentenceDto);

    }
