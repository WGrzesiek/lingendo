package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.ResponseSentenceDto;
import reactor.core.publisher.Mono;

public interface SentenceService {
    Mono<ResponseSentenceDto> getSentenceById(String id);
}
