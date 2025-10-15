package com.learnwords.vocabularyreadservice.service.grpc;

import com.learnwords.sentence.v1.*;
import io.grpc.stub.StreamObserver;

public interface SentenceServiceGrpc {
    void getSentenceById(GetSentenceByIdRequest request, StreamObserver<SentenceResponse> response);
    void batchGetSentencesByIds(BatchGetSentencesByIdsRequest request, StreamObserver<ListSentencesResponse> response);
    void listSentences(ListSentencesRequest request, StreamObserver<ListSentencesResponse> response);
}
