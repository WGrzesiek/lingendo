package com.learnwords.vocabularyreadservice.service.grpc.impl;


import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.sentence.v1.GetSentenceRequest;
import com.learnwords.sentence.v1.Sentence;
import com.learnwords.sentence.v1.SentenceReadServiceGrpc;
import com.learnwords.vocabularyreadservice.service.SentenceService;
import com.learnwords.vocabularyreadservice.service.grpc.SentenceServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

@Slf4j
@GrpcService
public class SentenceServiceGrpcImpl extends SentenceReadServiceGrpc.SentenceReadServiceImplBase implements SentenceServiceGrpc {

    private final SentenceService sentenceService;

    public SentenceServiceGrpcImpl(SentenceService sentenceService) {
        this.sentenceService = sentenceService;
    }

    @Override
    public void getSentence(GetSentenceRequest request, StreamObserver<Sentence> responseObserver) {
        log.info("Pobieranie zdania o id: {}", request.getId());
        sentenceService.getSentenceById(request.getId())
                .map(this::mapToGrpcSentence)
                .subscribe(
                        sentence -> {
                            responseObserver.onNext(sentence);
                            responseObserver.onCompleted();
                        },
                        error -> {
                            log.error("Błąd podczas pobierania zdania", error);
                            responseObserver.onError(error);
                        }
                );
    }

    private Sentence mapToGrpcSentence(ResponseSentenceDto dto) {
        return Sentence.newBuilder()
                .setId(dto.id())
                .setSentence(dto.sentence())
                .setTranslation(dto.translation())
                .build();
    }
}
