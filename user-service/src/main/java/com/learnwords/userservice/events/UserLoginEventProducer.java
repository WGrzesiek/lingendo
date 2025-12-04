package com.learnwords.userservice.events;

import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.UserLoginEvent;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class UserLoginEventProducer {

    private final KafkaTemplate<String, UserLoginEvent> kafkaTemplate;

    public UserLoginEventProducer(KafkaTemplate<String, UserLoginEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
    public void send(UserLoginEvent event) {
        kafkaTemplate.send(KafkaTopic.USER_LOGINS_TOPIC, event);
    }

}
