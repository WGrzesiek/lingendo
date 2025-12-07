package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.FlashcardAnsweredEvent;
import com.learnwords.common.events.FlashcardCreatedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

@Repository
public class FlashcardRepository {
    private final JdbcTemplate jdbcTemplate;

    public FlashcardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_FLASHCARD_CREATED_SQL = """
        INSERT INTO analytics.flashcard_answers
            (event_time, flashcard_id, deck_id, user_id, received_at)
        VALUES (?, ?, ?, ?, ?)
        """;

    public void saveFlashcardCreated(FlashcardCreatedEvent event) {
        jdbcTemplate.update(
                INSERT_FLASHCARD_CREATED_SQL,
                event.eventTime(),
                event.flashcardId(),
                event.deckId(),
                event.userId(),
                event.receivedAt());
    }

    private static final String INSERT_FLASHCARD_ANSWERED_SQL = """

            INSERT INTO analytics.flashcard_answers
                              (event_time, user_id, deck_enrollment_id, session_id, flashcard_id, correct, received_at)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    public void saveFlashcardAnswered(FlashcardAnsweredEvent event) {
        jdbcTemplate.update(
                INSERT_FLASHCARD_ANSWERED_SQL,
                event.eventTime(),
                event.userId(),
                event.deckEnrollmentId(),
                event.sessionId(),
                event.flashcardId(),
                event.correct(),
                event.receivedAt());
    }

}
