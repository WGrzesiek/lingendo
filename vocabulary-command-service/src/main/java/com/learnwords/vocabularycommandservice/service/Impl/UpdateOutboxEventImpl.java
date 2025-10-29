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

@Service
@Slf4j
public class UpdateOutboxEventImpl implements UpdateOutboxEvent {

    private final OutboxRepository outboxRepository;

    public UpdateOutboxEventImpl(OutboxRepository outboxRepository) {
        this.outboxRepository = outboxRepository;
    }

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
