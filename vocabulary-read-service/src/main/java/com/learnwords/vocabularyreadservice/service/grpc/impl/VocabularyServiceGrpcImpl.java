package com.learnwords.vocabularyreadservice.service.grpc.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.vocabulary.v1.*;
import com.learnwords.vocabularyreadservice.enums.FetchStrategy;
import com.learnwords.vocabularyreadservice.exception.exceptions.VocabularyNotFoundException;
import com.learnwords.vocabularyreadservice.service.VocabularyService;
import com.learnwords.vocabularyreadservice.service.grpc.VocabularyServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@GrpcService
public class VocabularyServiceGrpcImpl extends VocabularyReadServiceGrpc.VocabularyReadServiceImplBase  {


    private final VocabularyService vocabularyService;

    public VocabularyServiceGrpcImpl(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }


    @Override
    public void batchGetVocabularies(BatchGetVocabulariesRequest request, StreamObserver<BatchGetVocabulariesResponse> response){
        log.info("Pobieranie słów o id: {}", request.getIdsList());
        Mono<List<ResponseVocabularyDto>> vocabulariesDto = vocabularyService.getVocabulariesByIds(request.getIdsList());

        var vocabularies = vocabulariesDto.map(dtos -> {
            BatchGetVocabulariesResponse.Builder builder = BatchGetVocabulariesResponse.newBuilder();
            dtos.forEach(dto -> builder.addVocabularies(Vocabulary.newBuilder()
                    .setId(dto.id())
                    .setWord(dto.word())
                    .addAllTranslations(dto.translation() != null ? dto.translation() : new ArrayList<>())
                    .addAllSentenceIds(dto.sentenceIds() != null ? dto.sentenceIds() : new ArrayList<>())
                    .build()));
            return builder.build();
        });
        response.onNext(vocabularies.block());
        response.onCompleted();
    }

//    @Override
//    public void batchGetOnlyWord(BatchGetVocabulariesRequest request, StreamObserver<BatchGetOnlyWordResponse> output){
//        log.info("Pobieranie słów o id: {}", request.getIdsList());
//        vocabularyService.getOnlyWordsByIds(request.getIdsList())
//                .map(this::mapToGrpcOnlyWords)
//                .map(list -> BatchGetOnlyWordResponse.newBuilder()
//                        .addAllWord(list)
//                        .build())
//                .subscribe(
//                        response -> {output.onCompleted();},
//                        err -> {output.onError(mapToStatus(err));}
//                );
//    }


}
