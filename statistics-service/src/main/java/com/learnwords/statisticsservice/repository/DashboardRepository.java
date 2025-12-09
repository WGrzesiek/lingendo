package com.learnwords.statisticsservice.repository;

import com.learnwords.statisticsservice.dto.LeaderboardEntryDto;
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

    private static final String GET_MONTHLY_LEADERBOARD_WITH_CHANGES_SQL = """
    WITH
                    toStartOfMonth(today())      AS cur_month,
                    addMonths(cur_month, -1)     AS prev_month
                SELECT
                    cur.user_id                  AS user_id,
                    cur.username                 AS username,
                    cur.points                   AS points_current,
                    cur.rank                     AS rank_current,
                    prev.points                  AS points_previous,
                    prev.rank                    AS rank_previous,
                    (cur.points - coalesce(prev.points, 0))        AS points_diff,
                    (coalesce(prev.rank, 100000) - cur.rank)       AS rank_change
                FROM
                (
                    SELECT
                        user_id,
                        points,
                        username,
                        dense_rank() OVER (ORDER BY points DESC) AS rank
                    FROM analytics.user_points_monthly
                    WHERE month = cur_month
                ) cur
                LEFT JOIN
                (
                    SELECT
                        user_id,
                        points,
                        dense_rank() OVER (ORDER BY points DESC) AS rank
                    FROM analytics.user_points_monthly
                    WHERE month = prev_month
                ) prev USING (user_id)
                ORDER BY rank_current
                LIMIT 6;
            """;

    public List<LeaderboardEntryDto> getMonthlyLeaderboardWithChanges() {
        return jdbcTemplate.query(
                GET_MONTHLY_LEADERBOARD_WITH_CHANGES_SQL,
                (rs, rowNum) -> new LeaderboardEntryDto(
                        rs.getString("user_id"),
                        rs.getString("username"),
                        rs.getLong("points_current"),
                        rs.getLong("points_diff"),
                        rs.getInt("rank_current"),
                        rs.getInt("rank_previous")
                )
        );
    }


}
