package com.learnwords.vocabularyreadservice.service.grpc;

import com.learnwords.vocabulary.v1.BatchGetOnlyWordResponse;
import com.learnwords.vocabulary.v1.BatchGetVocabulariesRequest;
import com.learnwords.vocabulary.v1.BatchGetVocabulariesResponse;
import io.grpc.stub.StreamObserver;

public interface VocabularyServiceGrpc {
    void batchGetVocabularies(BatchGetVocabulariesRequest request, StreamObserver<BatchGetVocabulariesResponse> output);
    void batchGetOnlyWord(BatchGetVocabulariesRequest request, StreamObserver<BatchGetOnlyWordResponse> output);
}
