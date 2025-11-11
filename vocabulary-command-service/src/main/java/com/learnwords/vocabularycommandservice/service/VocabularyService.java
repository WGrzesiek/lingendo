package com.learnwords.vocabularycommandservice.service;

import com.learnwords.vocabularycommandservice.dto.CreateWordDto;
import com.learnwords.vocabularycommandservice.dto.SendWordDto;

import java.util.List;

public interface VocabularyService {
    SendWordDto createVocabulary(CreateWordDto createWordDto);
    SendWordDto createVocabularyForDeck(CreateWordDto createWordDto, String deckId);
    List<SendWordDto> createVocabularies(List<CreateWordDto> createWordDtos);
    List<SendWordDto> createVocabulariesForDeck(List<CreateWordDto> createWordDtos, String deckId);
}
