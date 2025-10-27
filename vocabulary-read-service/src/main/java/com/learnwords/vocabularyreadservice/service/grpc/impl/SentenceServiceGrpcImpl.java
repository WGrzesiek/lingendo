package com.learnwords.vocabularyreadservice.service.grpc.impl;


import com.learnwords.common.dto.ResponseSentenceDto;
import com.learnwords.sentence.v1.*;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import com.learnwords.vocabularyreadservice.service.SentenceService;
import com.learnwords.vocabularyreadservice.service.grpc.SentenceServiceGrpc;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;

@Slf4j
@GrpcService
public class SentenceServiceGrpcImpl extends SentenceReadServiceGrpc.SentenceReadServiceImplBase implements SentenceServiceGrpc {

    private final SentenceService sentenceService;

    public SentenceServiceGrpcImpl(SentenceService sentenceService) {
        this.sentenceService = sentenceService;
    }

    @Override
    public void getSentenceById(GetSentenceByIdRequest request, StreamObserver<SentenceResponse> response) {
        try {
            log.info("Pobieranie zdania o id: {}", request.getId());
            Optional<ResponseSentenceDto> sentenceDto = sentenceService.getSentenceById(request.getId());

            var sentences = sentenceDto.map(dto -> SentenceResponse.newBuilder()
                    .setId(dto.id())
                    .setSentence(dto.sentence())
                    .setTranslation(dto.translation())
                    .build());
            response.onNext(sentences.orElseThrow(() -> new RuntimeException("Nie znaleziono zdania o id: " + request.getId())));
            response.onCompleted();
        }
        catch (Exception e) {
            log.error("Błąd podczas pobierania zdania", e);
            response.onError(e);
        }
    }

    @Override
    public void batchGetSentencesByIds(BatchGetSentencesByIdsRequest request, StreamObserver<ListSentencesResponse> response) {
        try{
            log.info("Pobieranie zdań o id: {}", request.getIdsList());
            List<ResponseSentenceDto> sentenceDtos = sentenceService.getSentencesByIds(request.getIdsList());
            ListSentencesResponse.Builder builder = ListSentencesResponse.newBuilder();

            sentenceDtos.forEach(dto -> builder.addSentences(SentenceResponse.newBuilder()
                    .setId(dto.id())
                    .setSentence(dto.sentence())
                    .setTranslation(dto.translation())
                    .build()));

            response.onNext(builder.build());
            response.onCompleted();
        }
        catch (Exception e) {
            log.error("Błąd podczas pobierania zdań", e);
            response.onError(e);
        }
    }

    @Override
    public void listSentences(ListSentencesRequest request, StreamObserver<ListSentencesResponse> response) {
        try {
            log.info("Pobieranie {} zdań, strategia: {}", request.getPageSize(), request.getFetchStrategy());
            List<ResponseSentenceDto> sentenceDtos = sentenceService.getSentences(request.getPageSize(), FetchStrategy.valueOf(request.getFetchStrategy()));

            ListSentencesResponse.Builder builder = ListSentencesResponse.newBuilder();
            sentenceDtos.forEach(dto -> builder.addSentences(SentenceResponse.newBuilder()
                    .setId(dto.id())
                    .setSentence(dto.sentence())
                    .setTranslation(dto.translation())
                    .build()));

            response.onNext(builder.build());
            response.onCompleted();
        }
        catch (Exception e) {
            log.error("Błąd podczas pobierania zdań", e);
            response.onError(e);
        }
    }
}
