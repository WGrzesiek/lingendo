package com.learnwords.vocabularycommandservice.service;

import com.learnwords.common.dto.UpdateOutboxEventDto;

/**
 * Serwis do aktualizacji statusu zdarzeń w tabeli Outbox.
 * 
 * <p>Interfejs definiuje operacje związane z aktualizacją statusu zdarzeń
 * przechowywanych w tabeli Outbox Pattern. Jest używany przez mechanizm
 * publikacji zdarzeń do oznaczania wiadomości jako wysłane lub błędne.
 * 
 * <p>Outbox Pattern zapewnia, że zdarzenia są publikowane w sposób
 * transakcyjny - najpierw zapisywane w bazie danych, a następnie
 * asynchronicznie publikowane do message brokera (Kafka).
 * 
 * <p>Typowy przepływ:
 * <ol>
 *   <li>Zdarzenie zapisywane w tabeli outbox ze statusem PENDING</li>
 *   <li>Scheduler pobiera zdarzenia PENDING</li>
 *   <li>Próbuje opublikować do Kafki</li>
 *   <li>Wywołuje updateOutboxEvent() aby oznaczyć jako SENT lub ERROR</li>
 * </ol>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see UpdateOutboxEventDto
 */
public interface UpdateOutboxEvent {
     void updateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto);
}
