package com.learnwords.deckservice.service.grpcClient.impl;

import com.learnwords.deckservice.service.grpcClient.VocabularyGrpcClient;
import com.learnwords.vocabulary.v1.*;
import io.grpc.Deadline;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Implementacja klienta gRPC do komunikacji z Vocabulary Read Service.
 * 
 * <p>Klasa używa {@code BlockingStub} do synchronicznych wywołań gRPC
 * dla operacji odczytu słówek i zdań. Wszystkie wywołania mają ustawiony
 * timeout (deadline) aby uniknąć długiego blokowania.
 * 
 * <p>Obsługuje błędy gRPC poprzez przechwytywanie {@link StatusRuntimeException}
 * i rzucanie domenowego wyjątku z kontekstem błędu.
 * 
 * <p>Konfiguracja gRPC Client:
 * <ul>
 *   <li>Nazwa serwisu: {@code vocabulary-read}</li>
 *   <li>Typ stub: {@code BlockingStub} (synchroniczny)</li>
 *   <li>Timeout: 800ms dla wszystkich wywołań</li>
 * </ul>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-12
 * @see VocabularyGrpcClient
 * @see VocabularyReadServiceGrpc
 */
@Component
@Slf4j
public class VocabularyGrpcClientImpl implements VocabularyGrpcClient {

    /**
     * Timeout (deadline) dla wywołań gRPC w ms. Konfigurowalny przez {@code grpc.deadline-ms}.
     * Default 5000ms — 800ms było za mało na batch uderzający w Mongo Atlas przy native cold start
     * (DEADLINE_EXCEEDED na generateSentences).
     */
    @Value("${grpc.deadline-ms:5000}")
    private long grpcDeadlineMs;

    @GrpcClient("vocabulary-read")
    private VocabularyReadServiceGrpc.VocabularyReadServiceBlockingStub blockingStub;

    /**
     * Pobiera pełne informacje o słówkach (słowo + tłumaczenia + zdania) w trybie batch.
     * 
     * <p><strong>Metoda zdeprecjonowana.</strong> Używaj {@link #batchGetOnlyWord(List)} 
     * jeśli nie potrzebujesz zdań, lub {@link #batchGetWordsByIds(List)} dla pełnych danych.
     * 
     * @param ids lista ID słówek do pobrania
     * @return response z listą słówek wraz z pełnymi danymi
     * @throws RuntimeException jeśli wystąpi błąd gRPC (timeout, UNAVAILABLE, itp.)
     * @deprecated Używaj {@link #batchGetWordsByIds(List)} zamiast tej metody
     */
    @Deprecated
    @Override
    public BatchGetVocabulariesResponse batchGetVocabularies(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return BatchGetVocabulariesResponse.getDefaultInstance();
        }
        log.debug("Pobieranie {} słówek przez gRPC (DEPRECATED)", ids.size());
        
        try {
            BatchGetVocabulariesRequest request = BatchGetVocabulariesRequest.newBuilder()
                    .addAllIds(ids)
                    .build();
            
            return blockingStub
                    .withDeadline(Deadline.after(grpcDeadlineMs, TimeUnit.MILLISECONDS))
                    .batchGetVocabularies(request);
                    
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania słówek (batch): status={}, opis={}", 
                    e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Nie można pobrać słówek z Vocabulary Service: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * Pobiera tylko słowa i tłumaczenia (bez zdań) w trybie batch.
     * 
     * <p>Zoptymalizowana metoda do pobierania słówek bez przykładowych zdań.
     * Używaj gdy:
     * <ul>
     *   <li>Wyświetlasz listę słówek bez szczegółów</li>
     *   <li>Chcesz zmniejszyć obciążenie sieci</li>
     *   <li>Nie potrzebujesz zdań przykładowych</li>
     * </ul>
     * 
     * @param ids lista ID słówek do pobrania
     * @return response z listą słów i tłumaczeń (bez zdań)
     * @throws RuntimeException jeśli wystąpi błąd gRPC (timeout, UNAVAILABLE, itp.)
     */
    @Override
    public BatchGetOnlyWordResponse batchGetOnlyWord(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return BatchGetOnlyWordResponse.getDefaultInstance();
        }
        log.debug("Pobieranie {} słów (bez zdań) przez gRPC", ids.size());
        
        try {
            BatchGetVocabulariesRequest request = BatchGetVocabulariesRequest.newBuilder()
                    .addAllIds(ids)
                    .build();
            
            BatchGetOnlyWordResponse response = blockingStub
                    .withDeadline(Deadline.after(grpcDeadlineMs, TimeUnit.MILLISECONDS))
                    .batchGetOnlyWord(request);
            
            log.debug("Pomyślnie pobrano {} słów", response.getWordCount());
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania słów (only word): status={}, opis={}", 
                    e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Nie można pobrać słów z Vocabulary Service: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * Pobiera pojedyncze słówko po ID wraz ze wszystkimi danymi (tłumaczenia + zdania).
     * 
     * <p>Używaj gdy potrzebujesz szczegółowych informacji o konkretnym słówku,
     * w tym przykładowych zdań. Dla listy słówek użyj {@link #batchGetWordsByIds(List)}.
     * 
     * @param id ID słówka do pobrania
     * @return response z pełnymi danymi słówka
     * @throws RuntimeException jeśli wystąpi błąd gRPC lub słówko nie istnieje
     */
    @Override
    public GetWordResponse getWordById(String id) {
        log.debug("Pobieranie słówka o ID: {} przez gRPC", id);
        
        try {
            GetWordRequest request = GetWordRequest.newBuilder()
                    .setId(id)
                    .build();
            
            GetWordResponse response = blockingStub
                    .withDeadline(Deadline.after(grpcDeadlineMs, TimeUnit.MILLISECONDS))
                    .getWord(request);
            
            log.debug("Pomyślnie pobrano słówko: {}", response.getWord().getWord());
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania słówka {}: status={}, opis={}", 
                    id, e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Nie można pobrać słówka z Vocabulary Service: " + e.getStatus().getDescription(), e);
        }
    }

    /**
     * Pobiera wiele słówek po ID wraz z pełnymi danymi (tłumaczenia + zdania) w trybie batch.
     * 
     * <p>Najbardziej kompletna metoda do pobierania słówek. Używaj gdy:
     * <ul>
     *   <li>Potrzebujesz pełnych danych (słowo + tłumaczenia + zdania)</li>
     *   <li>Pobierasz wiele słówek naraz (batch operation)</li>
     *   <li>Wyświetlasz szczegóły słówek z przykładami</li>
     * </ul>
     * 
     * <p>Dla operacji bez zdań użyj {@link #batchGetOnlyWord(List)} (szybsze, mniej danych).
     * 
     * @param ids lista ID słówek do pobrania
     * @return response z listą słówek wraz z pełnymi danymi
     * @throws RuntimeException jeśli wystąpi błąd gRPC (timeout, UNAVAILABLE, itp.)
     */
    @Override
    public BatchGetWordsResponse batchGetWordsByIds(List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            // Nothing to fetch — skip the gRPC call (vocab-read rejects empty id lists).
            return BatchGetWordsResponse.getDefaultInstance();
        }
        log.debug("Pobieranie {} słówek (z pełnymi danymi) przez gRPC", ids.size());

        try {
            BatchGetWordsRequest request = BatchGetWordsRequest.newBuilder()
                    .addAllIds(ids)
                    .build();
            
            BatchGetWordsResponse response = blockingStub
                    .withDeadline(Deadline.after(grpcDeadlineMs, TimeUnit.MILLISECONDS))
                    .batchGetWords(request);
            
            log.debug("Pomyślnie pobrano {} słówek z pełnymi danymi", response.getWordsCount());
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("Błąd gRPC podczas pobierania słówek (batch words): status={}, opis={}", 
                    e.getStatus().getCode(), e.getStatus().getDescription());
            throw new RuntimeException("Nie można pobrać słówek z Vocabulary Service: " + e.getStatus().getDescription(), e);
        }
    }
}

