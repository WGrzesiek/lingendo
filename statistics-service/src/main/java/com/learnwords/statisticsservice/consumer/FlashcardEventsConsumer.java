package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.FlashcardAnsweredEvent;
import com.learnwords.common.events.FlashcardCreatedEvent;
import com.learnwords.statisticsservice.repository.FlashcardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlashcardEventsConsumer {

    private final FlashcardRepository repository;

    @KafkaListener(topics = KafkaTopic.FLASHCARD_CREATED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeFlashcardCreatedEvent(FlashcardCreatedEvent event) {
        try {
            log.debug("Odebrano FlashcardCreatedEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());

            repository.saveFlashcardCreated(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu FlashcardCreatedEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.FLASHCARD_ANSWERED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeFlashcardAnsweredEvent(FlashcardAnsweredEvent event) {
        try {
            log.debug("Odebrano FlashcardAnsweredEvent dla userId={} at={}",
                    event.userId(), event.receivedAt());

            repository.saveFlashcardAnswered(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu FlashcardAnsweredEvent do ClickHouse dla userId={}: {}",
                    event.userId(), e.getMessage(), e);
        }

    }
}
