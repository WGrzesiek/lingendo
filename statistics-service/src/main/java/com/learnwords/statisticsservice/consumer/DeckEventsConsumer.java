package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckCreatedEvent;
import com.learnwords.statisticsservice.repository.DeckRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;


@Slf4j
@Service
@RequiredArgsConstructor
public class DeckEventsConsumer {

    private final DeckRepository repository;

    @KafkaListener(topics = KafkaTopic.DECK_CREATED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeDeckCreatedEvent(DeckCreatedEvent event) {
        try {
            log.debug("Odebrano DeckCreatedEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());

            repository.saveDeckCreated(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu DeckCreatedEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }
}