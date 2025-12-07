package com.learnwords.statisticsservice.consumer;


import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.UserLoginEvent;
import com.learnwords.statisticsservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserEventsConsumer {

    private final UserRepository repository;

    @KafkaListener(topics = KafkaTopic.USER_LOGINS_TOPIC, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consume(UserLoginEvent event) {
        try {
            log.debug("Odebrano UserLoggedInEvent dla userId={} at={}",
                    event.userId(), event.received_at());

            repository.save(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu UserLoggedInEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }
}