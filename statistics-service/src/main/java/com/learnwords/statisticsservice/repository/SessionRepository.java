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
            (event_time, session_id, user_id, deck_id, deck_name, deck_enrollment_id, received_at)
        VALUES (?, ?, ?, ?, ?, ?,?)
        """;

    public void saveSessionStarted(SessionStartedEvent event) {
        jdbcTemplate.update(
                INSERT_SESSION_STARTED_SQL,
                event.eventTime(),
                event.sessionId(),
                event.userId(),
                event.deckId(),
                event.deckName(),
                event.deckEnrollmentId(),
                event.receivedAt()
        );
    }

    private static final String INSERT_SESSION_FINISHED_SQL = """
        INSERT INTO analytics.sessions_finished
            (event_time, started_at, session_id, user_id, deck_id, deck_name, deck_enrollment_id,correct_answers,incorrect_answers,received_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    public void saveSessionFinished(SessionFinishedEvent event) {
        jdbcTemplate.update(
                INSERT_SESSION_FINISHED_SQL,
                event.eventTime(),
                event.startedAt(),
                event.sessionId(),
                event.userId(),
                event.deckId(),
                event.deckName(),
                event.deckEnrollmentId(),
                event.correctAnswers(),
                event.incorrectAnswers(),
                event.receivedAt()
        );
    }

    private static final String GET_COMPLETED_SESSIONS_COUNT_BY_USER_SQL = """
        SELECT count(*) AS completed_sessions
        FROM analytics.sessions_finished
        WHERE user_id = ?
        """;

    public int getCompletedSessionsCountByUser(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_COMPLETED_SESSIONS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getInt("completed_sessions"),
                userId
        );
        return result != null ? result : 0;
    }

    private static final String GET_AVERAGE_ANSWERS_PER_SESSION_SQL = """
        WITH answer_per_session AS (
            SELECT COUNT() AS answers_per_session
            FROM analytics.flashcard_answers
            WHERE user_id = ?
            GROUP BY session_id
        ),
        average AS (
            SELECT avg(answers_per_session) AS avg_answers
            FROM answer_per_session
        )
        SELECT avg_answers
        FROM average;
        """;

    public Double getAverageAnswersPerSession(String userId) {
        return jdbcTemplate.queryForObject(
                GET_AVERAGE_ANSWERS_PER_SESSION_SQL,
                (rs, rowNum) -> rs.getDouble("avg_answers"),
                userId
        );
    }
}
