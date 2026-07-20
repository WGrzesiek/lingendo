package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.FlashcardAnsweredEvent;
import com.learnwords.common.events.FlashcardCreatedEvent;
import com.learnwords.statisticsservice.dto.course.FlashcardAnswersStats;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

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
                java.sql.Timestamp.from(event.eventTime()),
                event.flashcardId(),
                event.deckId(),
                event.userId(),
                java.sql.Timestamp.from(event.receivedAt()));
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
                java.sql.Timestamp.from(event.eventTime()),
                event.userId(),
                event.deckEnrollmentId(),
                event.sessionId(),
                event.flashcardId(),
                (event.correct() != null && event.correct()) ? 1 : 0,
                java.sql.Timestamp.from(event.receivedAt()),
                timeTakenMs);
    }

    private static final String GET_FLASHCARD_ANSWERS_SQL = """
        SELECT
            COUNT(*) AS total_answers,
            SUM(CASE WHEN correct = 1 THEN 1 ELSE 0 END) AS correct_answers,
            min(time_taken_ms) AS fastest_response_time,
            max(time_taken_ms) AS slowest_response_time,
            avg(time_taken_ms) AS average_response_time,
            SUM(time_taken_ms) AS total_time_taken,
            max(event_time) AS last_answered_at,
            SUM(CASE WHEN time_taken_ms < 30000 THEN 1 ELSE 0 END) AS answers_under_30_seconds

        FROM analytics.flashcard_answers
        WHERE analytics.flashcard_answers.deck_enrollment_id = ?
        """;

    private static final String GET_FLASHCARD_ANSWERS_SQL_WITH_DATE = """
        SELECT
            COUNT(*) AS total_answers,
            SUM(CASE WHEN correct = 1 THEN 1 ELSE 0 END) AS correct_answers,
            min(time_taken_ms) AS fastest_response_time,
            max(time_taken_ms) AS slowest_response_time,
            avg(time_taken_ms) AS average_response_time,
            SUM(time_taken_ms) AS total_time_taken,
            max(event_time) AS last_answered_at,
            SUM(CASE WHEN time_taken_ms < 30000 THEN 1 ELSE 0 END) AS answers_under_30_seconds

        FROM analytics.flashcard_answers
        WHERE analytics.flashcard_answers.deck_enrollment_id = ?
          AND event_time >= ?
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
                    int until30SecAnswers = rs.getInt("answers_under_30_seconds");

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
                            lastSessionDate,
                            until30SecAnswers
                    );
                },
                deckEnrollmentId
        );
    }
    public FlashcardAnswersStats getFlashcardAnswersStats(String deckEnrollmentId, Integer lastDays) {
            if (lastDays == null || lastDays <= 0) {
                return getFlashcardAnswersStats(deckEnrollmentId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        return jdbcTemplate.queryForObject(
                GET_FLASHCARD_ANSWERS_SQL_WITH_DATE,
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
                    int until30SecAnswers = rs.getInt("answers_under_30_seconds");

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
                            lastSessionDate,
                            until30SecAnswers
                    );
                },
                deckEnrollmentId,
                ts
        );
    }

    private static final String GET_CREATED_FLASHCARDS_COUNT_BY_USER_SQL = """
        SELECT count(*) AS created_flashcards
        FROM analytics.flashcard_answers
        WHERE user_id = ?
        """;

    private static final String GET_CREATED_FLASHCARDS_COUNT_BY_USER_SQL_WITH_DATE = """
        SELECT count(*) AS created_flashcards
        FROM analytics.flashcard_answers
        WHERE user_id = ? AND event_time >= ?
        """;

    public int getCreatedFlashcardsCountByUser(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_CREATED_FLASHCARDS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getInt("created_flashcards"),
                userId
        );
        return result != null ? result : 0;
    }

    public int getCreatedFlashcardsCountByUser(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getCreatedFlashcardsCountByUser(userId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        Integer result = jdbcTemplate.queryForObject(
                GET_CREATED_FLASHCARDS_COUNT_BY_USER_SQL_WITH_DATE,
                (rs, rowNum) -> rs.getInt("created_flashcards"),
                userId,
                ts
        );
        return result != null ? result : 0;
    }

    private static final String GET_ANSWERED_FLASHCARDS_COUNT_BY_USER_SQL = """
        SELECT COUNT(flashcard_id) AS answered_flashcards,
        SUM(CASE WHEN correct = 1 THEN 1 ELSE 0 END) AS correct_answers
        FROM analytics.flashcard_answers
        WHERE user_id = ?
        """;

    private static final String GET_ANSWERED_FLASHCARDS_COUNT_BY_USER_SQL_WITH_DATE = """
        SELECT COUNT(flashcard_id) AS answered_flashcards,
        SUM(CASE WHEN correct = 1 THEN 1 ELSE 0 END) AS correct_answers
        FROM analytics.flashcard_answers
        WHERE user_id = ? AND event_time >= ?
        """;

    public Map<String, Integer> getAnsweredFlashcardsCountByUser(String userId) {
        return jdbcTemplate.queryForObject(
                GET_ANSWERED_FLASHCARDS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> {
                    Map<String, Integer> map = new HashMap<>();
                    map.put("answered_flashcards", rs.getInt("answered_flashcards"));
                    map.put("correct_answers", rs.getInt("correct_answers"));
                    return map;
                },
                userId
        );
    }

    public Map<String, Integer> getAnsweredFlashcardsCountByUser(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getAnsweredFlashcardsCountByUser(userId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        return jdbcTemplate.queryForObject(
                GET_ANSWERED_FLASHCARDS_COUNT_BY_USER_SQL_WITH_DATE,
                (rs, rowNum) -> {
                    Map<String, Integer> map = new HashMap<>();
                    map.put("answered_flashcards", rs.getInt("answered_flashcards"));
                    map.put("correct_answers", rs.getInt("correct_answers"));
                    return map;
                },
                userId,
                ts
        );
    }
}
