package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.common.dto.WordDto;

import java.util.List;
import java.util.Optional;

public interface VocabularyService {

    Optional<ResponseVocabularyDto> getVocabularyById(String id);
    List<ResponseVocabularyDto> getVocabulariesByIds(List<String> ids);
    List<OnlyWordDto> getOnlyWordsByIds(List<String> ids);

    Optional<WordDto> getWordById(String id);
    List<WordDto> getWordsByIds(List<String> ids);
}
