package com.learnwords.vocabularyreadservice.service.grpc.impl;

import com.learnwords.common.dto.ResponseVocabularyDto;
import com.learnwords.common.dto.WordDto;
import com.learnwords.sentence.v1.SentenceResponse;
import com.learnwords.vocabulary.v1.*;
import com.learnwords.vocabularyreadservice.service.VocabularyService;

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
public class VocabularyServiceGrpcImpl extends VocabularyReadServiceGrpc.VocabularyReadServiceImplBase  {


    private final VocabularyService vocabularyService;

    public VocabularyServiceGrpcImpl(VocabularyService vocabularyService) {
        this.vocabularyService = vocabularyService;
    }

    // ===== NOWE METODY (zwracają Word z pełnymi danymi) =====

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

    // ===== STARE METODY (DEPRECATED) =====

    /**
     * Pobiera słowa według listy ID (stara wersja - tylko ID zdań).
     * 
     * @param request Request zawierający listę ID
     * @param response Observer do wysłania odpowiedzi
     * @deprecated Od wersji 2.0. Użyj {@link #batchGetWords(BatchGetWordsRequest, StreamObserver)} zamiast tego.
     */
    @Deprecated(since = "2.0", forRemoval = true)
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


}
