package com.learnwords.vocabularycommandservice.service.Impl;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.vocabularycommandservice.repository.OutboxRepository;
import com.learnwords.vocabularycommandservice.service.UpdateOutboxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementacja serwisu do aktualizacji statusu zdarzeń w tabeli Outbox.
 * 
 * <p>Klasa nasłuchuje na topik Kafka z aktualizacjami statusów zdarzeń
 * i aktualizuje odpowiednie rekordy w tabeli Outbox. Jest częścią mechanizmu
 * Outbox Pattern, który zapewnia transakcyjną publikację zdarzeń.
 * 
 * <p>Przepływ działania:
 * <ol>
 *   <li>Scheduler publikuje zdarzenie z tabeli Outbox do Kafki</li>
 *   <li>Po pomyślnej publikacji, scheduler wysyła komunikat na topik {@link KafkaTopic#UPPATED_STATUS}</li>
 *   <li>Ta klasa nasłuchuje na ten topik i aktualizuje status w bazie danych</li>
 *   <li>Zdarzenie oznaczane jako SENT (sukces) lub ERROR (błąd)</li>
 * </ol>
 * 
 * <p>Używa {@code @KafkaListener} do automatycznego odbierania wiadomości
 * i {@code @Transactional} do zapewnienia atomowości aktualizacji.
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see UpdateOutboxEvent
 * @see OutboxRepository
 */
@Service
@Slf4j
public class UpdateOutboxEventImpl implements UpdateOutboxEvent {

    private final OutboxRepository outboxRepository;

    public UpdateOutboxEventImpl(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

    /**
     * Aktualizuje status zdarzenia w tabeli Outbox na podstawie komunikatu z Kafki.
     * 
     * <p>Metoda jest Kafka Listenerem nasłuchującym na topik {@link KafkaTopic#UPPATED_STATUS}.
     * Gdy scheduler pomyślnie opublikuje zdarzenie do Kafki, wysyła komunikat na ten topik,
     * który jest przechwytywany przez tę metodę.
     * 
     * <p>Proces aktualizacji:
     * <ol>
     *   <li>Odebrane DTO zawiera aggregateId (ID zdarzenia) i nowy status</li>
     *   <li>Wywołanie {@link OutboxRepository#updateOutboxEventStatus} aktualizuje rekord</li>
     *   <li>Status zmieniony na SENT (sukces) lub ERROR (błąd)</li>
     *   <li>W przypadku wyjątku rzuca {@code RuntimeException}</li>
     * </ol>
     * 
     * <p>Transakcja zapewnia, że aktualizacja jest atomowa - albo całkowicie
     * pomyślna, albo rollback w przypadku błędu.
     * 
     * <p>Konfiguracja Kafka Listener:
     * <ul>
     *   <li>Topic: {@link KafkaTopic#UPPATED_STATUS}</li>
     *   <li>Group ID: {@link KafkaGroup#OUTBOX_EVENT_SERVICE_GROUP}</li>
     *   <li>Deserializacja: automatyczna do {@link UpdateOutboxEventDto}</li>
     * </ul>
     * 
     * @param updateOutboxEventDto DTO zawierające ID zdarzenia (aggregateId) i nowy status (eventStatus)
     * @throws RuntimeException jeśli wystąpi błąd podczas aktualizacji statusu w bazie danych
     */
    @Override
    @Transactional
    @KafkaListener(topics = KafkaTopic.UPPATED_STATUS, groupId = KafkaGroup.OUTBOX_EVENT_SERVICE_GROUP,
            properties = {
                    "spring.json.value.default.type=com.learnwords.common.dto.UpdateOutboxEventDto"
            })
    public void updateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto) {
        try{
            outboxRepository.updateOutboxEventStatus(updateOutboxEventDto.aggregateId(), updateOutboxEventDto.eventStatus().toString());
        } catch (Exception e){
            throw new RuntimeException("Błąd podczas aktualizacji statusu eventu w outbox: " + e.getMessage(), e);
        }
    }

}
