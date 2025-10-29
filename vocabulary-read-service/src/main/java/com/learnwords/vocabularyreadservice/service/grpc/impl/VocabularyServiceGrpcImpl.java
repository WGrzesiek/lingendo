package com.learnwords.vocabularyreadservice.service.grpc.impl;

import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.vocabulary.v1.*;
import com.learnwords.vocabularyreadservice.service.VocabularyService;

import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

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
        List<ResponseVocabularyDto> vocabulariesDto = vocabularyService.getVocabulariesByIds(request.getIdsList());
        BatchGetVocabulariesResponse.Builder builder = BatchGetVocabulariesResponse.newBuilder();
        try {
            vocabulariesDto.forEach(dto -> builder.addVocabularies(Vocabulary.newBuilder()
                    .setId(dto.id())
                    .setWord(dto.word())
                    .addAllTranslations(dto.translation() != null ? dto.translation() : new ArrayList<>())
                    .addAllSentenceIds(dto.sentenceIds() != null ? dto.sentenceIds() : new ArrayList<>())
                    .build()));

            response.onNext(builder.build());
            response.onCompleted();
        }
        catch (Exception e){
            log.error("Błąd podczas pobierania słów", e);
            response.onError(e);
        }
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
