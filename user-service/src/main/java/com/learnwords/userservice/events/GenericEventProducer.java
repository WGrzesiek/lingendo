package com.learnwords.userservice.events;

import com.learnwords.common.events.DomainEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class GenericEventProducer {

    private final KafkaTemplate<String, DomainEvent> kafkaTemplate;

    public GenericEventProducer(KafkaTemplate<String, DomainEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(String topic, DomainEvent event) {
        kafkaTemplate.send(topic, event);
    }
}
