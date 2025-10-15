package com.learnwords.deckservice.service.GrpcClient;

import com.learnwords.vocabulary.v1.BatchGetOnlyWordResponse;
import com.learnwords.vocabulary.v1.BatchGetVocabulariesResponse;

import java.util.List;

public interface VocabularyGrpcClient {
    BatchGetVocabulariesResponse batchGetVocabularies(List<String> ids);
    BatchGetOnlyWordResponse batchGetOnlyWord(List<String> ids);
}
