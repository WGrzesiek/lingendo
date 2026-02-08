package com.learnwords.vocabularyreadservice.service.grpc.impl;

import com.learnwords.common.dto.OnlyWordDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.sentence.v1.SentenceResponse;
import com.learnwords.vocabulary.v1.*;
import com.learnwords.vocabularyreadservice.service.VocabularyService;

import com.learnwords.vocabularyreadservice.service.grpc.VocabularyServiceGrpc;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementacja gRPC serwisu do odczytu słownictwa.
 * 
 * <p>Serwis udostępnia metody gRPC do pobierania słów i ich szczegółów
 * dla innych mikroserwisów (np. deck-service).
 * 
 * <p><b>Uwaga:</b> Metody zwracające {@link Vocabulary} są deprecated.
 * Preferuj użycie metod zwracających {@link Word} z pełnymi danymi.
 * 
 * @author Grzegorz Wawrzeń
 * @version 2.0
 * @since 2025-11-11
 */
@Slf4j
@GrpcService
public class VocabularyServiceGrpcImpl extends VocabularyReadServiceGrpc.VocabularyReadServiceImplBase  implements VocabularyServiceGrpc {


    private final VocabularyService vocabularyService;

    public VocabularyServiceGrpcImpl(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    /**
     * Pobiera pełne dane pojedynczego słowa według ID.
     * 
     * @param request Request zawierający ID słowa
     * @param responseObserver Observer do wysłania odpowiedzi
     */
    @Override
    public void getWord(GetWordRequest request, StreamObserver<GetWordResponse> responseObserver) {
        log.info("Pobieranie pełnego słowa o id: {}", request.getId());
        
        try {
            Optional<WordDto> wordDto = vocabularyService.getWordById(request.getId());
            
            if (wordDto.isEmpty()) {
                log.warn("Nie znaleziono słowa o id: {}", request.getId());
                responseObserver.onError(
                    Status.NOT_FOUND
                        .withDescription("Word not found: " + request.getId())
                        .asRuntimeException()
                );
                return;
            }
            
            Word word = convertToProtoWord(wordDto.get());
            GetWordResponse response = GetWordResponse.newBuilder()
                .setWord(word)
                .build();
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            log.info("Pomyślnie pobrano słowo o id: {}", request.getId());
        } catch (Exception e) {
            log.error("Błąd podczas pobierania słowa o id: {}", request.getId(), e);
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * Pobiera pełne dane wielu słów według listy ID.
     * 
     * @param request Request zawierający listę ID słów
     * @param responseObserver Observer do wysłania odpowiedzi
     */
    @Override
    public void batchGetWords(BatchGetWordsRequest request, StreamObserver<BatchGetWordsResponse> responseObserver) {
        log.info("Pobieranie {} pełnych słów", request.getIdsCount());
        
        try {
            List<WordDto> wordsDto = vocabularyService.getWordsByIds(request.getIdsList());
            
            BatchGetWordsResponse.Builder builder = BatchGetWordsResponse.newBuilder();
            wordsDto.forEach(dto -> builder.addWords(convertToProtoWord(dto)));
            
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            log.info("Pomyślnie pobrano {} słów", wordsDto.size());
        } catch (Exception e) {
            log.error("Błąd podczas pobierania słów", e);
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * Pobiera minimalne dane wielu słów według listy ID.
     *
     * @param request Request zawierający listę ID słów
     * @param responseObserver Observer do wysłania odpowiedzi
     */
    @Override
    public void batchGetOnlyWord(BatchGetVocabulariesRequest request, StreamObserver<BatchGetOnlyWordResponse> responseObserver) {
        log.info("Pobieranie {} minimalnych słów", request.getIdsCount());

        try {
            List<OnlyWordDto> onlyWordsDto = vocabularyService.getOnlyWordsByIds(request.getIdsList());

            BatchGetOnlyWordResponse.Builder builder = BatchGetOnlyWordResponse.newBuilder();
            onlyWordsDto.forEach(dto ->
                builder.addWord(
                    OnlyWord.newBuilder()
                        .setId(dto.id())
                        .setWord(dto.word())
                        .build()
            ));

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();
            log.info("Pomyślnie pobrano {} minimalnych słów", onlyWordsDto.size());
        } catch (Exception e) {
            log.error("Błąd podczas pobierania minimalnych słów", e);
            responseObserver.onError(
                Status.INTERNAL
                    .withDescription("Internal error: " + e.getMessage())
                    .asRuntimeException()
            );
        }
    }

    /**
     * Konwertuje {@link WordDto} na protobuf {@link Word}.
     * 
     * @param dto WordDto z serwisu
     * @return Word protobuf message
     */
    private Word convertToProtoWord(WordDto dto) {
        return Word.newBuilder()
            .setId(dto.id())
            .setWord(dto.word())
            .addAllTranslations(dto.translations() != null ? dto.translations() : new ArrayList<>())
            .addAllSentences(dto.sentences() != null 
                ? dto.sentences().stream()
                    .map(s -> SentenceResponse.newBuilder()
                        .setId(s.id())
                        .setSentence(s.sentence())
                        .setTranslation(s.translation())
                        .build())
                    .toList()
                : new ArrayList<>())
            .addAllSentencesAi(dto.sentencesAI() != null
                ? dto.sentencesAI().stream()
                    .map(s -> SentenceResponse.newBuilder()
                        .setId(s.id())
                        .setSentence(s.sentence())
                        .setTranslation(s.translation())
                        .build())
                    .toList()
                : new ArrayList<>())
            .build();
    }


}
