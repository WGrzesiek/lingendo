package com.learnwords.vocabularyreadservice.service.grpc.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.vocabulary.v1.*;
import com.learnwords.vocabularyreadservice.exception.exceptions.VocabularyNotFoundException;
import com.learnwords.vocabularyreadservice.service.VocabularyService;
import com.learnwords.vocabularyreadservice.service.grpc.VocabularyServiceGrpc;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;


@Slf4j
@GrpcService
public class VocabularyServiceGrpcImpl extends VocabularyReadServiceGrpc.VocabularyReadServiceImplBase implements VocabularyServiceGrpc {


    private final VocabularyService vocabularyService;

    public VocabularyServiceGrpcImpl(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }


    @Override
    public void batchGetVocabularies(BatchGetVocabulariesRequest request, StreamObserver<BatchGetVocabulariesResponse> output){
        log.info("Pobieranie słów o id: {}", request.getIdsList());
        vocabularyService.getVocabulariesByIds(request.getIdsList())
                .map(this::mapToGrpcVocabularies)
                .map(list -> BatchGetVocabulariesResponse.newBuilder()
                        .addAllVocabularies(list)
                        .build())
                .subscribe(
                        response -> {output.onCompleted();},
                        err -> {output.onError(mapToStatus(err));}
                );
    }

    @Override
    public void batchGetOnlyWord(BatchGetVocabulariesRequest request, StreamObserver<BatchGetOnlyWordResponse> output){
        log.info("Pobieranie słów o id: {}", request.getIdsList());
        vocabularyService.getOnlyWordsByIds(request.getIdsList())
                .map(this::mapToGrpcOnlyWords)
                .map(list -> BatchGetOnlyWordResponse.newBuilder()
                        .addAllWord(list)
                        .build())
                .subscribe(
                        response -> {output.onCompleted();},
                        err -> {output.onError(mapToStatus(err));}
                );
    }

    private Vocabulary mapToGrpcVocabulary(ResponseVocabularyDto dto) {
        return Vocabulary.newBuilder()
                .setId(dto.id())
                .setWord(dto.word())
                .addAllTranslations(dto.translation())
                .addAllSentenceIds(dto.sentenceIds())
                .build();
    }

    private List<Vocabulary> mapToGrpcVocabularies(List<ResponseVocabularyDto> dto) {
        List<Vocabulary> vocabularies = new ArrayList<>();
        for(ResponseVocabularyDto vocabularyDto : dto) {
            Vocabulary vocabulary = mapToGrpcVocabulary(vocabularyDto);
            vocabularies.add(vocabulary);
        }
        return vocabularies;
    }

    private List<OnlyWord> mapToGrpcOnlyWords(List<OnlyWordDto> dto) {
        List<OnlyWord> onlyWords = new ArrayList<>();
        for(OnlyWordDto onlyWordDto : dto) {
            OnlyWord onlyWord = OnlyWord.newBuilder()
                    .setId(onlyWordDto.id())
                    .setWord(onlyWordDto.word())
                    .build();
            onlyWords.add(onlyWord);
        }
        return onlyWords;
    }

    private StatusRuntimeException mapToStatus(Throwable t) {
        if (t instanceof IllegalArgumentException) return Status.INVALID_ARGUMENT.withDescription(t.getMessage()).asRuntimeException();
        if (t instanceof TimeoutException)         return Status.DEADLINE_EXCEEDED.withDescription("Timeout").asRuntimeException();
        if (t instanceof VocabularyNotFoundException) return Status.NOT_FOUND.withDescription(t.getMessage()).asRuntimeException();
        return Status.INTERNAL.withDescription("Internal error").augmentDescription(t.getMessage()).asRuntimeException();
    }
}
