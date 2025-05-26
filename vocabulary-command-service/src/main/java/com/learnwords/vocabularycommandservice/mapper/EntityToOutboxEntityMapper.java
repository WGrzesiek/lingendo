package com.learnwords.vocabularycommandservice.mapper;

import com.learnwords.common.EventStatus;
import com.learnwords.common.EventType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.learnwords.vocabularycommandservice.entity.Outbox;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class EntityToOutboxEntityMapper {

    @SneakyThrows
    public Outbox map(String id, Object object, EventType eventType){
        return Outbox.builder()
                .id(id)
                .payload(new ObjectMapper().writeValueAsString(object))
                .createdAt(new Date())
                .updatedAt(new Date())
                .eventStatus(EventStatus.CREATED.name())
                .eventType(eventType.name())
                .build();
    }
}
