package com.learnwords.statisticsservice.consumer;

import com.learnwords.common.KafkaGroup;
import com.learnwords.common.KafkaTopic;
import com.learnwords.common.events.TeacherDeckSharedEvent;
import com.learnwords.common.events.TeacherStudentJoinedEvent;
import com.learnwords.common.events.TeacherStudentRemovedEvent;
import com.learnwords.statisticsservice.repository.TeacherDashboardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeacherStudentEventsConsumer {

    private final TeacherDashboardRepository repository;

    @KafkaListener(topics = KafkaTopic.TEACHER_STUDENT_JOINED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeStudentJoined(TeacherStudentJoinedEvent event) {
        try {
            log.debug("Odebrano TeacherStudentJoinedEvent: nauczyciel={}, uczeń={}", 
                    event.teacherId(), event.studentId());

            repository.addStudent(event.teacherId(), event.studentId());
            repository.addNewStudentActivity(
                    event.teacherId(), 
                    event.studentId(), 
                    event.studentUsername(),
                    event.eventTime()
            );
        } catch (Exception e) {
            log.error("Błąd podczas zapisu TeacherStudentJoinedEvent do ClickHouse: teacherId={}, studentId={}: {}",
                    event.teacherId(), event.studentId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.TEACHER_STUDENT_REMOVED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeStudentRemoved(TeacherStudentRemovedEvent event) {
        try {
            log.debug("Odebrano TeacherStudentRemovedEvent: nauczyciel={}, uczeń={}", 
                    event.teacherId(), event.studentId());

            repository.removeStudent(event.teacherId(), event.studentId());
        } catch (Exception e) {
            log.error("Błąd podczas zapisu TeacherStudentRemovedEvent do ClickHouse: teacherId={}, studentId={}: {}",
                    event.teacherId(), event.studentId(), e.getMessage(), e);
        }
    }

    @KafkaListener(topics = KafkaTopic.TEACHER_DECK_SHARED, groupId = KafkaGroup.STATISTICS_SERVICE)
    public void consumeDeckShared(TeacherDeckSharedEvent event) {
        try {
            log.debug("Odebrano TeacherDeckSharedEvent: nauczyciel={}, talia={}", 
                    event.teacherId(), event.deckId());

            repository.addSharedDeck(event.teacherId(), event.deckId(), event.deckName());
        } catch (Exception e) {
            log.error("Błąd podczas zapisu TeacherDeckSharedEvent do ClickHouse: teacherId={}, deckId={}: {}",
                    event.teacherId(), event.deckId(), e.getMessage(), e);
        }
    }
}
