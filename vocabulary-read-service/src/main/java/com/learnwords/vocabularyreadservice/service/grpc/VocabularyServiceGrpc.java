package com.learnwords.vocabularyreadservice.service.grpc;

import com.learnwords.vocabulary.v1.*;
import io.grpc.stub.StreamObserver;

/**
 * Interfejs gRPC serwisu do odczytu słownictwa.
 *
 * <p>Serwis udostępnia metody gRPC do pobierania słów i ich szczegółów
 * dla innych mikroserwisów (np. deck-service).
 *
 * @author Grzegorz Wawrzeń
 * @version 2.0
 * @since 2025-11-11
 */
public interface VocabularyServiceGrpc {
    void getWord(GetWordRequest request, StreamObserver<GetWordResponse> output);
    void batchGetWords(BatchGetWordsRequest request, StreamObserver<BatchGetWordsResponse> output);
    void batchGetOnlyWord(BatchGetVocabulariesRequest request, StreamObserver<BatchGetOnlyWordResponse> output);
}
