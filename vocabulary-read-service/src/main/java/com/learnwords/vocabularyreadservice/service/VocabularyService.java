package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VocabularyService {

    Mono<ResponseVocabularyDto> getVocabularyById(String id);
    Mono<List<ResponseVocabularyDto>> getVocabulariesByIds(List<String> ids);
    Mono<List<OnlyWordDto>> getOnlyWordsByIds(List<String> ids);

}
