package com.learnwords.vocabularyreadservice.service.grpc;

import com.learnwords.vocabulary.v1.*;
import io.grpc.stub.StreamObserver;

public interface VocabularyServiceGrpc {
    // ===== NOWE METODY (zwracają Word z pełnymi danymi) =====
    void getWord(GetWordRequest request, StreamObserver<GetWordResponse> output);
    void batchGetWords(BatchGetWordsRequest request, StreamObserver<BatchGetWordsResponse> output);
    
    // ===== STARE METODY (DEPRECATED) =====
    /**
     * @deprecated Od wersji 2.0. Użyj {@link #batchGetWords(BatchGetWordsRequest, StreamObserver)} zamiast tego.
     */
    @Deprecated(since = "2.0", forRemoval = true)
    void batchGetVocabularies(BatchGetVocabulariesRequest request, StreamObserver<BatchGetVocabulariesResponse> output);
    
    /**
     * @deprecated Od wersji 2.0. Ta metoda może pozostać dla lekkich zapytań.
     */
    @Deprecated(since = "2.0", forRemoval = false)
    void batchGetOnlyWord(BatchGetVocabulariesRequest request, StreamObserver<BatchGetOnlyWordResponse> output);
}
