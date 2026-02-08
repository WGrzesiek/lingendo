package com.learnwords.vocabularyreadservice.service.grpc;

import com.learnwords.sentence.v1.*;
import io.grpc.stub.StreamObserver;

/**
 * Interfejs gRPC serwisu do odczytu przykladowych zdań.
 *
 * <p>Serwis udostępnia metody gRPC do pobierania zdań
 * dla innych mikroserwisów (np. deck-service).
 *
 * @author Grzegorz Wawrzeń
 * @version 2.0
 * @since 2025-11-11
 */
public interface SentenceServiceGrpc {
    void getSentenceById(GetSentenceByIdRequest request, StreamObserver<SentenceResponse> response);
    void batchGetSentencesByIds(BatchGetSentencesByIdsRequest request, StreamObserver<ListSentencesResponse> response);
    void listSentences(ListSentencesRequest request, StreamObserver<ListSentencesResponse> response);
}
