package com.learnwords.vocabularycommandservice.service;

import com.learnwords.vocabularycommandservice.dto.CreateSentenceDto;
import com.learnwords.vocabularycommandservice.dto.SendSentenceDto;

public interface SentenceService {
    SendSentenceDto createSentence(CreateSentenceDto csd, String wordId);

}
