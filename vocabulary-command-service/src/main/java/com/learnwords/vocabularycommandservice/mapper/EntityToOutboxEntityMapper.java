package com.learnwords.vocabularycommandservice.mapper;

import com.learnwords.common.AggregateType;
import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class EntityToOutboxEntityMapper {

    private final ObjectMapper objectMapper;

    public EntityToOutboxEntityMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @SneakyThrows
    public Outbox map(String aggregateId,
                      AggregateType aggregateType,
                      Object payload,
                      EventType eventType) {

        return Outbox.builder()
                .eventId(UUID.randomUUID().toString())
                .aggregateId(aggregateId)
                .aggregateType(aggregateType)
                .eventType(eventType)
                .payload(objectMapper.writeValueAsString(payload))
                .eventStatus(EventStatus.CREATED)
                .build();
    }
}
