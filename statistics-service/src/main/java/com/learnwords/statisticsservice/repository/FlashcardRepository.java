package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.FlashcardAnsweredEvent;
import com.learnwords.common.events.FlashcardCreatedEvent;
import com.learnwords.statisticsservice.dto.course.FlashcardAnswersStats;
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
                              (event_time, user_id, deck_enrollment_id, session_id, flashcard_id, correct, received_at, time_taken_ms)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    public void saveFlashcardAnswered(FlashcardAnsweredEvent event) {
        long timeTakenMs = event.timeTaken().toMillis();

        jdbcTemplate.update(
                INSERT_FLASHCARD_ANSWERED_SQL,
                event.eventTime(),
                event.userId(),
                event.deckEnrollmentId(),
                event.sessionId(),
                event.flashcardId(),
                event.correct(),
                event.receivedAt(),
                timeTakenMs);
    }

    private static final String GET_FLASHCARD_ANSWERS_SQL = """
        SELECT
            COUNT(*) AS total_answers,
            SUM(CASE WHEN correct THEN 1 ELSE 0 END) AS correct_answers,
            min(time_taken_ms) AS fastest_response_time,
            max(time_taken_ms) AS slowest_response_time,
            avg(time_taken_ms) AS average_response_time,
            SUM(time_taken_ms) AS total_time_taken,
            max(event_time) AS last_answered_at

        FROM analytics.flashcard_answers
        WHERE analytics.flashcard_answers.deck_enrollment_id = ?
        """;

    public FlashcardAnswersStats getFlashcardAnswersStats(String deckEnrollmentId) {
        return jdbcTemplate.queryForObject(
                GET_FLASHCARD_ANSWERS_SQL,
                (rs, rowNum) -> {
                    int totalAnswers = rs.getInt("total_answers");
                    int correctAnswers = rs.getInt("correct_answers");
                    int incorrectAnswers = totalAnswers - correctAnswers;
                    int accuracy = totalAnswers > 0 ? (correctAnswers * 100) / totalAnswers : 0;
                    int averageResponseTime = rs.getInt("average_response_time");
                    int fastestResponse = rs.getInt("fastest_response_time");
                    int slowestResponse = rs.getInt("slowest_response_time");
                    int totalStudyTime = rs.getInt("total_time_taken");
                    Timestamp lastAnsweredAt = rs.getTimestamp("last_answered_at");
                    int lastSessionDate = lastAnsweredAt != null ? (int) (lastAnsweredAt.getTime() / 1000) : 0;

                    return new FlashcardAnswersStats(
                            deckEnrollmentId,
                            totalAnswers,
                            correctAnswers,
                            incorrectAnswers,
                            accuracy,
                            averageResponseTime,
                            totalStudyTime,
                            fastestResponse,
                            slowestResponse,
                            lastSessionDate
                    );
                },
                deckEnrollmentId
        );
    }

}
