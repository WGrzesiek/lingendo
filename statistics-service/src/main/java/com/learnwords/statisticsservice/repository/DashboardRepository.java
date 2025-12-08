package com.learnwords.statisticsservice.repository;

import com.learnwords.statisticsservice.dto.StudentActivityItemDto;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;

@Repository
public class DashboardRepository {

    private final JdbcTemplate jdbcTemplate;

    public DashboardRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String GET_ACTIVE_DECKS_SQL = """
        SELECT countDistinct(deck_id) AS active_decks
        FROM analytics.sessions_started
        WHERE user_id = ?
          AND event_time >= now() - INTERVAL 30 DAY
        """;

    private static final String GET_COMPLETED_LESSONS_THIS_MONTH_SQL = """
        SELECT countDistinct(session_id) AS completed_lessons
        FROM analytics.sessions_finished
        WHERE user_id = ?
          AND toStartOfMonth(event_time) = toStartOfMonth(today())
        """;

    // streak: ile kolejnych dni od dzisiaj w dół user miał aktywność
    private static final String GET_STREAK_SQL = """
        SELECT count() AS streak_days
        FROM (
            SELECT
                day,
                row_number() OVER (ORDER BY day DESC) AS rn
            FROM (
                SELECT DISTINCT toDate(event_time) AS day
                FROM analytics.sessions_started
                WHERE user_id = ?
                  AND event_time <= now()
                  AND event_time >= today() - INTERVAL 90 DAY
            )
        )
        WHERE day = today() - (rn - 1)
        """;

    private static final String GET_USER_POINTS_SQL = """
        SELECT
            sum(points) AS total_points,
            sumIf(points, day >= toStartOfWeek(today())) AS points_this_week
        FROM analytics.user_points_daily
        WHERE user_id = ?
        """;

    public int getActiveDecks(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_ACTIVE_DECKS_SQL,
                Integer.class,
                userId
        );
        return result != null ? result : 0;
    }

    public int getCompletedLessonsThisMonth(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_COMPLETED_LESSONS_THIS_MONTH_SQL,
                Integer.class,
                userId
        );
        return result != null ? result : 0;
    }

    public int getStreakDays(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_STREAK_SQL,
                Integer.class,
                userId
        );
        return result != null ? result : 0;
    }

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

    private static final String GET_RECENT_ACTIVITY_SQL = """
        SELECT
            event_time,
            type,
            title,
            points
        FROM analytics.user_activity
        WHERE user_id = ?
        ORDER BY event_time DESC
        LIMIT ?
        """;

    public List<StudentActivityItemDto> getRecentActivity(String userId, int limit) {
        return jdbcTemplate.query(
                GET_RECENT_ACTIVITY_SQL,
                (rs, rowNum) -> new StudentActivityItemDto(
                        rs.getString("type"),
                        rs.getString("title"),
                        rs.getInt("points"),
                        rs.getObject("event_time", Timestamp.class).toInstant()
                ),
                userId,
                limit
        );
    }
}
