package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.vocabularyreadservice.service.UpdateOutboxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

/**
 * Implementacja serwisu aktualizującego status eventów outbox.
 * 
 * <p>Serwis odpowiedzialny za wysyłanie aktualizacji statusu przetwarzanych eventów
 * do topiku Kafka UPDATED_STATUS. Umożliwia śledzenie stanu przetwarzania eventów
 * w systemie event-driven.
 * 
 * <p>Główne funkcjonalności:
 * <ul>
 *   <li>Wysyłanie aktualizacji statusu do Kafka (RECEIVED, PROCESSING, COMPLETED, FAILED)</li>
 *   <li>Asynchroniczne potwierdzanie wysłania z obsługą błędów</li>
 *   <li>Walidacja danych wejściowych</li>
 *   <li>Logowanie wszystkich operacji</li>
 * </ul>
 */
@Slf4j
@Service
public class UpdateOutboxEventImpl implements UpdateOutboxEvent {
    
    private final KafkaTemplate<String, UpdateOutboxEventDto> kafkaTemplate;

    public UpdateOutboxEventImpl(KafkaTemplate<String, UpdateOutboxEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Przetwarza i wysyła aktualizację statusu eventu outbox do Kafka.
     * 
     * <p>Metoda:
     * <ul>
     *   <li>Waliduje dane wejściowe (DTO nie może być null)</li>
     *   <li>Wysyła event do topiku UPDATED_STATUS</li>
     *   <li>Asynchronicznie obsługuje potwierdzenie wysłania</li>
     *   <li>Loguje sukces lub błąd operacji</li>
     * </ul>
     * 
     * @param updateOutboxEventDto DTO zawierające aggregateId i nowy status eventu
     * @throws IllegalArgumentException gdy updateOutboxEventDto jest null
     */
    @Override
    public void processUpdateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto) {
        if (updateOutboxEventDto == null) {
            log.error("UpdateOutboxEventDto nie może być null");
            throw new IllegalArgumentException("UpdateOutboxEventDto nie może być null");
        }

        if (updateOutboxEventDto.aggregateId() == null || updateOutboxEventDto.aggregateId().isBlank()) {
            log.error("AggregateId nie może być puste");
            throw new IllegalArgumentException("AggregateId nie może być puste");
        }

        if (updateOutboxEventDto.eventStatus() == null) {
            log.error("EventStatus nie może być null");
            throw new IllegalArgumentException("EventStatus nie może być null");
        }

        try {
            CompletableFuture<SendResult<String, UpdateOutboxEventDto>> future = 
                    kafkaTemplate.send(KafkaTopic.UPPATED_STATUS, updateOutboxEventDto);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    log.error("Błąd podczas wysyłania eventu aktualizacji statusu dla aggregateId: {}, status: {}, błąd: {}", 
                            updateOutboxEventDto.aggregateId(), 
                            updateOutboxEventDto.eventStatus(), 
                            ex.getMessage());
                } else {
                    log.info("Wysłano event aktualizacji statusu dla aggregateId: {}, status: {}", 
                            updateOutboxEventDto.aggregateId(), 
                            updateOutboxEventDto.eventStatus());
                }
            });
        } catch (Exception e) {
            log.error("Nieoczekiwany błąd podczas wysyłania eventu dla aggregateId: {}: {}", 
                    updateOutboxEventDto.aggregateId(), 
                    e.getMessage(), 
                    e);
            throw new RuntimeException("Błąd podczas wysyłania eventu aktualizacji statusu", e);
        }
    }
}
