package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.FriendshipAcceptedEvent;
import com.learnwords.common.events.FriendshipRemovedEvent;
import com.learnwords.statisticsservice.repository.FriendshipStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FriendshipEventsConsumer {

    private final FriendshipStatsRepository repository;

    @KafkaListener(topics = KafkaTopic.FRIENDSHIP_ACCEPTED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeFriendshipAccepted(FriendshipAcceptedEvent event) {
        try {
            log.debug("Odebrano FriendshipAcceptedEvent: {} <-> {}", 
                    event.userId1(), event.userId2());

            repository.saveFriendshipAccepted(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu FriendshipAcceptedEvent do ClickHouse: {} <-> {}: {}",
                    event.userId1(), event.userId2(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.FRIENDSHIP_REMOVED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeFriendshipRemoved(FriendshipRemovedEvent event) {
        try {
            log.debug("Odebrano FriendshipRemovedEvent: {} <-> {}, powód={}", 
                    event.userId1(), event.userId2(), event.reason());

            repository.saveFriendshipRemoved(event);
        } catch (Exception e) {
            log.error("Błąd podczas zapisu FriendshipRemovedEvent do ClickHouse: {} <-> {}: {}",
                    event.userId1(), event.userId2(), e.getMessage(), e);
        }
    }
}
