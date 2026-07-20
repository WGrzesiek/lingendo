package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.SessionFinishedEvent;
import com.learnwords.common.events.SessionStartedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

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
                java.sql.Timestamp.from(event.eventTime()),
                event.sessionId(),
                event.userId(),
                event.deckId(),
                event.deckName(),
                event.deckEnrollmentId(),
                java.sql.Timestamp.from(event.receivedAt())
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
                java.sql.Timestamp.from(event.eventTime()),
                event.startedAt(),
                event.sessionId(),
                event.userId(),
                event.deckId(),
                event.deckName(),
                event.deckEnrollmentId(),
                event.correctAnswers(),
                event.incorrectAnswers(),
                java.sql.Timestamp.from(event.receivedAt())
        );
    }

    private static final String GET_COMPLETED_SESSIONS_COUNT_BY_USER_SQL = """
        SELECT count(*) AS completed_sessions
        FROM analytics.sessions_finished
        WHERE user_id = ?
        """;

    private static final String GET_COMPLETED_SESSIONS_COUNT_BY_USER_SQL_WITH_DATE =
        """
            SELECT count(*) AS completed_sessions
            FROM analytics.sessions_finished
            WHERE user_id = ? AND event_time >= ?
        """;

    /**
     * Zwraca liczbę ukończonych sesji dla użytkownika za cały dostępny okres.
     *
     * @param userId id użytkownika
     */
    public int getCompletedSessionsCountByUser(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_COMPLETED_SESSIONS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getInt("completed_sessions"),
                userId
        );
        return result != null ? result : 0;
    }
    /**
     * Zwraca liczbę ukończonych sesji dla użytkownika.
     *
     * @param userId   id użytkownika
     * @param lastDays jeśli null lub <= 0 — używa całego okresu; w przeciwnym wypadku liczy od teraz minus lastDays dni
     */
    public int getCompletedSessionsCountByUser(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getCompletedSessionsCountByUser(userId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        Integer result = jdbcTemplate.queryForObject(
                GET_COMPLETED_SESSIONS_COUNT_BY_USER_SQL_WITH_DATE,
                (rs, rowNum) -> rs.getInt("completed_sessions"),
                userId,
                ts
        );
        return result != null ? result : 0;
    }

    private static final String GET_AVERAGE_ANSWERS_PER_SESSION_SQL = """
        WITH answer_per_session AS (
            SELECT COUNT(*) AS answers_per_session
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

    private static final String GET_AVERAGE_ANSWERS_PER_SESSION_SQL_WITH_DATE = """
        WITH answer_per_session AS (
            SELECT COUNT(*) AS answers_per_session
            FROM analytics.flashcard_answers
            WHERE user_id = ? AND event_time >= ?
            GROUP BY session_id
        ),
        average AS (
            SELECT avg(answers_per_session) AS avg_answers
            FROM answer_per_session
        )
        SELECT avg_answers
        FROM average;
        """;

    /**
     * Zwraca średnią odpowiedzi na sesję dla użytkownika za cały dostępny okres.
     *
     * @param userId id użytkownika
     */
    public Double getAverageAnswersPerSession(String userId) {
        Double result = jdbcTemplate.queryForObject(
                GET_AVERAGE_ANSWERS_PER_SESSION_SQL,
                (rs, rowNum) -> rs.getDouble("avg_answers"),
                userId
        );
        return result != null ? result : 0.0;
    }

    /**
     * Zwraca średnią odpowiedzi na sesję dla użytkownika.
     *
     * @param userId   id użytkownika
     * @param lastDays jeśli null lub <= 0 — używa całego okresu; w przeciwnym wypadku liczy od teraz minus lastDays dni
     */
    public Double getAverageAnswersPerSession(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getAverageAnswersPerSession(userId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        Double result = jdbcTemplate.queryForObject(
                GET_AVERAGE_ANSWERS_PER_SESSION_SQL_WITH_DATE,
                (rs, rowNum) -> rs.getDouble("avg_answers"),
                userId,
                ts
        );
        return result != null ? result : 0.0;
    }
}
