package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.common.events.DeckEnrollmentsFinished;
import com.learnwords.statisticsservice.repository.DeckEnrollmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeckEnrollmentEventsConsumer {

    private final DeckEnrollmentRepository repository;

    @KafkaListener(topics = KafkaTopic.DECK_ENROLLMENT_CREATED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeDeckEnrollmentCreated(DeckEnrollmentsCreated event) {
        try {
            log.debug("Odebrano DeckCreatedEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());

            repository.saveDeckEnrollmentCreate(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu DeckCreatedEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.DECK_ENROLLMENT_FINISHED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeDeckEnrollmentFinished(DeckEnrollmentsFinished event) {
        try {
            log.debug("Odebrano DeckFinishedEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());
            repository.saveDeckEnrollmentFinished(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu DeckFinishedEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }
}

