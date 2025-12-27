package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.DeckSharedEvent;
import com.learnwords.common.events.GroupCreatedEvent;
import com.learnwords.common.events.GroupMemberAddedEvent;
import com.learnwords.common.events.GroupMemberRemovedEvent;
import com.learnwords.statisticsservice.repository.GroupStatisticsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupEventsConsumer {

    private final GroupStatisticsRepository repository;

    @KafkaListener(topics = KafkaTopic.GROUP_CREATED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeGroupCreated(GroupCreatedEvent event) {
        try {
            log.debug("Odebrano GroupCreatedEvent: grupa={}, nauczyciel={}", 
                    event.groupId(), event.teacherId());

            repository.createGroup(
                    event.groupId(),
                    event.groupName(),
                    event.teacherId(),
                    event.eventTime()
            );
        } catch (Exception e) {
            log.error("Błąd podczas zapisu GroupCreatedEvent do ClickHouse: groupId={}: {}",
                    event.groupId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.GROUP_MEMBER_ADDED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeMemberAdded(GroupMemberAddedEvent event) {
        try {
            log.debug("Odebrano GroupMemberAddedEvent: grupa={}, uczeń={}", 
                    event.groupId(), event.studentId());

            repository.addMember(
                    event.groupId(),
                    event.studentId(),
                    event.teacherId(),
                    event.eventTime()
            );
        } catch (Exception e) {
            log.error("Błąd podczas zapisu GroupMemberAddedEvent do ClickHouse: groupId={}, studentId={}: {}",
                    event.groupId(), event.studentId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.GROUP_MEMBER_REMOVED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeMemberRemoved(GroupMemberRemovedEvent event) {
        try {
            log.debug("Odebrano GroupMemberRemovedEvent: grupa={}, uczeń={}", 
                    event.groupId(), event.studentId());

            repository.removeMember(event.groupId(), event.studentId());
        } catch (Exception e) {
            log.error("Błąd podczas zapisu GroupMemberRemovedEvent do ClickHouse: groupId={}, studentId={}: {}",
                    event.groupId(), event.studentId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.DECK_SHARED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeDeckShared(DeckSharedEvent event) {
        try {
            if (!"GROUP".equals(event.targetType())) {
                return;
            }

            log.debug("Odebrano DeckSharedEvent dla grupy: deck={}, grupa={}", 
                    event.deckId(), event.targetId());

            repository.addSharedDeck(
                    event.targetId(),
                    event.deckId(),
                    event.deckName(),
                    event.ownerId(),
                    event.eventTime()
            );
        } catch (Exception e) {
            log.error("Błąd podczas zapisu DeckSharedEvent do ClickHouse: deckId={}, groupId={}: {}",
                    event.deckId(), event.targetId(), e.getMessage(), e);
        }
    }
}
