package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.common.events.DeckEnrollmentsFinished;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class DeckEnrollmentRepository {
    private final JdbcTemplate jdbcTemplate;

    public DeckEnrollmentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_DECK_ENROLLMENT_CREATED_SQL = """
        INSERT INTO analytics.deck_enrollments_created
            (event_time, deck_enrollment_id, deck_id, deck_name, user_id, received_at)
        VALUES (?, ?, ?, ?, ?,?)
        """;

    public void saveDeckEnrollmentCreate(DeckEnrollmentsCreated event) {
        jdbcTemplate.update(
                INSERT_DECK_ENROLLMENT_CREATED_SQL,
                java.sql.Timestamp.from(event.eventTime()),
                event.deckEnrollmentId(),
                event.deckId(),
                event.deckName(),
                event.userId(),
                java.sql.Timestamp.from(Instant.now())
        );
    }

    private static final String INSERT_DECK_ENROLLMENT_FINISHED_SQL = """
        INSERT INTO analytics.deck_enrollments_finished
            (event_time, deck_enrollment_id, deck_id, deck_name, user_id, received_at)
        VALUES (?, ?, ?, ?, ?, ?)
        """;

    public void saveDeckEnrollmentFinished(DeckEnrollmentsFinished event) {
        jdbcTemplate.update(
                INSERT_DECK_ENROLLMENT_FINISHED_SQL,
                java.sql.Timestamp.from(event.eventTime()),
                event.deckEnrollmentId(),
                event.deckId(),
                event.deckName(),
                event.userId(),
                java.sql.Timestamp.from(Instant.now())
        );
    }

    private static final String GET_USER_POINTS_SQL = """
        SELECT
            sum(points) AS total_points,
            sum(points) FILTER (WHERE day >= date_trunc('week', current_date)) AS points_this_week
        FROM analytics.user_points_daily
        WHERE user_id = ?
    """;


    public UserPointsDto getUserPoints(String userId) {
        UserPointsDto result = jdbcTemplate.queryForObject(
                GET_USER_POINTS_SQL,
                (rs, rowNum) -> {
                    long totalPoints = rs.getLong("total_points");
                    long pointsThisWeek = rs.getLong("points_this_week");
                    return new UserPointsDto(
                            rs.wasNull() ? 0L : totalPoints,
                            rs.wasNull() ? 0L : pointsThisWeek
                    );
                },
                userId
        );
        return result != null ? result : new UserPointsDto(0L, 0L);
    }

    private static final String GET_TOTAL_STUDENTS_FOR_DECK_SQL = """
        SELECT COUNT(DISTINCT user_id) AS total_students
        FROM analytics.deck_enrollments_created
        WHERE deck_id = ?
    """;

    public Long getTotalStudentsForDeck(String deckId) {
        Long result = jdbcTemplate.queryForObject(
                GET_TOTAL_STUDENTS_FOR_DECK_SQL,
                (rs, rowNum) -> rs.getLong("total_students"),
                deckId
        );
        return result != null ? result : 0L;
    }

    private static final String GET_TOTAL_COMPLETED_STUDENTS_FOR_DECK_SQL = """
        SELECT COUNT(DISTINCT user_id) AS total_completed_students
        FROM analytics.deck_enrollments_finished
        WHERE deck_id = ?
    """;

    public Long getTotalCompletedStudentsForDeck(String deckId) {
        Long result = jdbcTemplate.queryForObject(
                GET_TOTAL_COMPLETED_STUDENTS_FOR_DECK_SQL,
                (rs, rowNum) -> rs.getLong("total_completed_students"),
                deckId
        );
        return result != null ? result : 0L;
    }

    private static final String  GET_TOTAL_STUDENTS_FOR_DECKS_SQL = """
        SELECT deck_id, COUNT(DISTINCT user_id) AS total_students
        FROM analytics.deck_enrollments_created
        WHERE deck_id IN (%s)
        GROUP BY deck_id
    """;

    public Map<String, Long> getTotalStudentsForDecks(List<String> deckIds) {
        String inSql = String.join(",", java.util.Collections.nCopies(deckIds.size(), "?"));
        String finalSql = String.format(GET_TOTAL_STUDENTS_FOR_DECKS_SQL, inSql);

        return jdbcTemplate.query(finalSql,
                rs -> {
                    java.util.Map<String, Long> result = new java.util.HashMap<>();
                    while (rs.next()) {
                        result.put(rs.getString("deck_id"), rs.getLong("total_students"));
                    }
                    return result;
                },
                deckIds.toArray()
        );
    }

    private static final String GET_TOTAL_COMPLETED_STUDENTS_FOR_DECKS_SQL = """
        SELECT deck_id, COUNT(DISTINCT user_id) AS total_completed_students
        FROM analytics.deck_enrollments_finished
        WHERE deck_id IN (%s)
        GROUP BY deck_id
    """;

    public Map<String, Long> getTotalCompletedStudentsForDecks(List<String> deckIds) {
        String inSql = String.join(",", Collections.nCopies(deckIds.size(), "?"));
        String finalSql = String.format(GET_TOTAL_COMPLETED_STUDENTS_FOR_DECKS_SQL, inSql);

        return jdbcTemplate.query(finalSql,
                rs -> {
                    Map<String, Long> result = new HashMap<>();
                    while (rs.next()) {
                        result.put(rs.getString("deck_id"), rs.getLong("total_completed_students"));
                    }
                    return result;
                },
                deckIds.toArray()
        );
    }

    private static final String GET_DECK_ENROLLMENTS_COUNT_BY_USER_SQL = """
        SELECT COUNT(*) AS enrollments_count
        FROM analytics.deck_enrollments_created
        WHERE user_id = ?
    """;

    private static final String GET_DECK_ENROLLMENTS_COUNT_BY_USER_SQL_WITH_DATE = """
        SELECT COUNT(*) AS enrollments_count
        FROM analytics.deck_enrollments_created
        WHERE user_id = ? AND event_time >= ?
    """;

    public Long getDeckEnrollmentsCountByUser(String userId) {
        Long result = jdbcTemplate.queryForObject(
                GET_DECK_ENROLLMENTS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getLong("enrollments_count"),
                userId
        );
        return result != null ? result : 0L;
    }

    public Long getDeckEnrollmentsCountByUser(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getDeckEnrollmentsCountByUser(userId);
        }
        Instant since = Instant.now().minusSeconds(lastDays * 24L * 60L * 60L);
        Long result = jdbcTemplate.queryForObject(
                GET_DECK_ENROLLMENTS_COUNT_BY_USER_SQL_WITH_DATE,
                (rs, rowNum) -> rs.getLong("enrollments_count"),
                userId,
                Timestamp.from(since)
        );
        return result != null ? result : 0L;
    }

    private static final String GET_DECK_COMPLETIONS_COUNT_BY_USER_SQL = """
        SELECT COUNT(*) AS completions_count
        FROM analytics.deck_enrollments_finished
        WHERE user_id = ?
    """;

    private static final String GET_DECK_COMPLETIONS_COUNT_BY_USER_SQL_WITH_DATE = """
        SELECT COUNT(*) AS completions_count
        FROM analytics.deck_enrollments_finished
        WHERE user_id = ? AND event_time >= ?
    """;

    public Long getDeckCompletionsCountByUser(String userId) {
        Long result = jdbcTemplate.queryForObject(
                GET_DECK_COMPLETIONS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getLong("completions_count"),
                userId
        );
        return result != null ? result : 0L;
    }

    public Long getDeckCompletionsCountByUser(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getDeckCompletionsCountByUser(userId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        Long result = jdbcTemplate.queryForObject(
                GET_DECK_COMPLETIONS_COUNT_BY_USER_SQL_WITH_DATE,
                (rs, rowNum) -> rs.getLong("completions_count"),
                userId,
                ts
        );
        return result != null ? result : 0L;
    }
}
