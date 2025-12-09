package com.learnwords.deckservice.service.event;


import com.learnwords.common.KafkaTopic;
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
