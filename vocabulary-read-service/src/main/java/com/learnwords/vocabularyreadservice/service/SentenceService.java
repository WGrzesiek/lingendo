package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;

import java.util.List;
import java.util.Optional;

public interface SentenceService {
    Optional<ResponseSentenceDto> getSentenceById(String id);
    List<ResponseSentenceDto> getSentencesByIds(List<String> ids);
    List<ResponseSentenceDto> getSentences(int page_size, FetchStrategy fetchStrategy);
}
