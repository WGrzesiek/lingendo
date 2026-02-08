package com.learnwords.vocabularyreadservice.service;

import com.learnwords.common.dto.UpdateOutboxEventDto;

/**
 * Serwis aktualizujący status eventów outbox.
 * 
 * <p>Odpowiada za wysyłanie aktualizacji statusu przetwarzanych eventów
 * do topiku Kafka w celu śledzenia ich stanu w systemie.
 */
public interface UpdateOutboxEvent {

    void processUpdateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto);
}
