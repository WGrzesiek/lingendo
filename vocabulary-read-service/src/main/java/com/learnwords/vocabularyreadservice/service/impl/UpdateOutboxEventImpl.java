package com.learnwords.vocabularyreadservice.service.impl;

import com.learnwords.common.EventStatus;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.dto.UpdateOutboxEventDto;
import com.learnwords.vocabularyreadservice.service.UpdateOutboxEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UpdateOutboxEventImpl implements UpdateOutboxEvent {
    private final KafkaTemplate<String, UpdateOutboxEventDto> kafkaTemplate;

    public UpdateOutboxEventImpl(KafkaTemplate<String, UpdateOutboxEventDto> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    @Override
    public void processUpdateOutboxEvent(UpdateOutboxEventDto updateOutboxEventDto) {
        kafkaTemplate.send(KafkaTopic.UPPATED_STATUS, updateOutboxEventDto);
        log.info("Wysłano event aktualizacji statusu dla aggregateId: {}, status: {}", updateOutboxEventDto.aggregateId(), updateOutboxEventDto.eventStatus());
    }
}
