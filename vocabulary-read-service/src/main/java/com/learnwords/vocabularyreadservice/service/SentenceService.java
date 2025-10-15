package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SentenceService {
    Mono<ResponseSentenceDto> getSentenceById(String id);
    Mono<List<ResponseSentenceDto>> getSentencesByIds(List<String> ids);
    Mono<List<ResponseSentenceDto>> getSentences(int page_size, FetchStrategy fetchStrategy);
}
