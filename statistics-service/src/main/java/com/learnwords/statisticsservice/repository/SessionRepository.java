package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.SessionFinishedEvent;
import com.learnwords.common.events.SessionStartedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class SessionRepository {
    private final JdbcTemplate jdbcTemplate;

    public SessionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SESSION_STARTED_SQL = """
        INSERT INTO analytics.sessions_started
            (event_time, session_id, user_id, deck_id, deck_enrollment_id, received_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    public void saveSessionStarted(SessionStartedEvent event) {
        jdbcTemplate.update(
                INSERT_SESSION_STARTED_SQL,
                event.eventTime(),
                event.sessionId(),
                event.userId(),
                event.deckId(),
                event.deckEnrollmentId(),
                event.receivedAt()
        );
    }

    private static final String INSERT_SESSION_FINISHED_SQL = """
        INSERT INTO analytics.sessions_finished
            (event_time, started_at, session_id, user_id, deck_id, deck_enrollment_id,correct_answers,incorrect_answers,received_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    public void saveSessionFinished(SessionFinishedEvent event) {
        jdbcTemplate.update(
                INSERT_SESSION_FINISHED_SQL,
                event.eventTime(),
                event.startedAt(),
                event.sessionId(),
                event.userId(),
                event.deckId(),
                event.deckEnrollmentId(),
                event.correctAnswers(),
                event.incorrectAnswers(),
                event.receivedAt()
        );
    }
}
