package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.common.events.DeckEnrollmentsFinished;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;
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
                event.eventTime(),
                event.deckEnrollmentId(),
                event.deckId(),
                event.deckName(),
                event.userId(),
                Instant.now()
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
                event.eventTime(),
                event.deckEnrollmentId(),
                event.deckId(),
                event.deckName(),
                event.userId(),
                Instant.now()
        );
    }

    private static final String GET_USER_POINTS_SQL = """
        SELECT
            sum(points) AS total_points,
            sumIf(points, day >= toStartOfWeek(today())) AS points_this_week
        FROM analytics.user_points_daily
        WHERE user_id = ?
    """;


    public UserPointsDto getUserPoints(String userId) {
        return jdbcTemplate.queryForObject(
                GET_USER_POINTS_SQL,
                (rs, rowNum) -> new UserPointsDto(
                        rs.getLong("total_points"),
                        rs.getLong("points_this_week")
                ),
                userId
        );
    }

    private static final String GET_TOTAL_STUDENTS_FOR_DECK_SQL = """
        SELECT COUNT(DISTINCT user_id) AS total_students
        FROM analytics.deck_enrollments_created
        WHERE deck_id = ?
    """;

    public Long getTotalStudentsForDeck(String deckId) {
        return jdbcTemplate.queryForObject(
                GET_TOTAL_STUDENTS_FOR_DECK_SQL,
                (rs, rowNum) -> rs.getLong("total_students"),
                deckId
        );
    }

    private static final String GET_TOTAL_COMPLETED_STUDENTS_FOR_DECK_SQL = """
        SELECT COUNT(DISTINCT user_id) AS total_completed_students
        FROM analytics.deck_enrollments_finished
        WHERE deck_id = ?
    """;

    public Long getTotalCompletedStudentsForDeck(String deckId) {
        return jdbcTemplate.queryForObject(
                GET_TOTAL_COMPLETED_STUDENTS_FOR_DECK_SQL,
                (rs, rowNum) -> rs.getLong("total_completed_students"),
                deckId
        );
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
        String inSql = String.join(",", java.util.Collections.nCopies(deckIds.size(), "?"));
        String finalSql = String.format(GET_TOTAL_COMPLETED_STUDENTS_FOR_DECKS_SQL, inSql);

        return jdbcTemplate.query(finalSql,
                rs -> {
                    java.util.Map<String, Long> result = new java.util.HashMap<>();
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

    public Long getDeckEnrollmentsCountByUser(String userId) {
        return jdbcTemplate.queryForObject(
                GET_DECK_ENROLLMENTS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getLong("enrollments_count"),
                userId
        );
    }

    private static final String GET_DECK_COMPLETIONS_COUNT_BY_USER_SQL = """
        SELECT COUNT(*) AS completions_count
        FROM analytics.deck_enrollments_finished
        WHERE user_id = ?
    """;

    public Long getDeckCompletionsCountByUser(String userId) {
        return jdbcTemplate.queryForObject(
                GET_DECK_COMPLETIONS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getLong("completions_count"),
                userId
        );
    }




}
