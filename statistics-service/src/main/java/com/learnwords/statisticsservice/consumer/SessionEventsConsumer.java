package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.SessionFinishedEvent;
import com.learnwords.common.events.SessionStartedEvent;
import com.learnwords.statisticsservice.repository.SessionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SessionEventsConsumer {
    private final SessionRepository repository;

    public SessionEventsConsumer(SessionRepository repository) {
        this.repository = repository;
    }

    @KafkaListener(topics = KafkaTopic.SESSION_STARTED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeSessionStartedEvent(SessionStartedEvent event) {
        try{
            log.debug("Odebrano SessionStartedEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());
            repository.saveSessionStarted(event);
        }
        catch (Exception e){
            log.error("Błąd podczas zapisu SessionStartedEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.SESSION_FINISHED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeSessionFinishedEvent(SessionFinishedEvent event) {
        try{
            log.debug("Odebrano SessionFinishedEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());
            repository.saveSessionFinished(event);
        }
        catch (Exception e){
            log.error("Błąd podczas zapisu SessionFinishedEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }
}
