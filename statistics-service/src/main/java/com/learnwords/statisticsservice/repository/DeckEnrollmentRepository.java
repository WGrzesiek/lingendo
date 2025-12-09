package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.DeckEnrollmentsCreated;
import com.learnwords.common.events.DeckEnrollmentsFinished;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.Instant;

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


}
