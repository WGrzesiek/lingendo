package com.learnwords.statisticsservice.repository;

import com.learnwords.statisticsservice.dto.leaderboard.LeaderboardEntryDto;
import com.learnwords.statisticsservice.dto.StudentActivityItemDto;
import com.learnwords.statisticsservice.dto.UserPointsDto;
import com.learnwords.statisticsservice.dto.leaderboard.LeaderboardOverviewDto;


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
        SELECT count(DISTINCT deck_id) AS active_decks
        FROM analytics.sessions_started
        WHERE user_id = ?
          AND event_time >= now() - INTERVAL '30 days'
        """;

    private static final String GET_COMPLETED_LESSONS_THIS_MONTH_SQL = """
        SELECT count(DISTINCT session_id) AS completed_lessons
        FROM analytics.sessions_finished
        WHERE user_id = ?
          AND date_trunc('month', event_time) = date_trunc('month', current_date)
        """;

    // streak: ile kolejnych dni od dzisiaj w dół user miał aktywność
    private static final String GET_STREAK_SQL = """
        SELECT analytics.user_activity.subtitle
        FROM analytics.user_activity
        WHERE analytics.user_activity.type = 'LOGIN' and analytics.user_activity.user_id = ?
        ORDER BY analytics.user_activity.event_time DESC
        LIMIT 1;
    """;

    private static final String GET_USER_POINTS_SQL = """
        SELECT
            sum(points) AS total_points,
            sum(points) FILTER (WHERE day >= date_trunc('week', current_date)) AS points_this_week
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
        String result = jdbcTemplate.queryForObject(
                GET_STREAK_SQL,
                String.class,
                userId
        );
        if (result == null || result.isEmpty()) {
            return 0;
        }
        String digits = result.replaceAll("\\D+", "");
        return digits.isEmpty() ? 0 : Integer.parseInt(digits);
    }

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

    private static final String GET_RECENT_ACTIVITY_SQL = """
        SELECT
            event_time,
            type,
            title,
            subtitle,
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
                        rs.getString("subtitle"),
                        rs.getInt("points"),
                        rs.getObject("event_time", Timestamp.class).toInstant()
                ),
                userId,
                limit
        );
    }


    private static final String GET_LEADERBOARD_FROM_SNAPSHOT_SQL = """
    WITH latest_snapshot AS (
        SELECT *
        FROM analytics.leaderboard_snapshot
    ),
    user_rank AS (
        SELECT rank
        FROM latest_snapshot
        WHERE user_id = ?
    )
    SELECT
        ls.*,
        CASE
            WHEN ls.rank = (SELECT rank FROM user_rank) - 1 THEN
                ls.total_points - (SELECT total_points FROM latest_snapshot WHERE user_id = ?)
            ELSE NULL
        END AS points_to_rank_up
    FROM latest_snapshot ls
    WHERE ls.rank <= 3
       OR ls.user_id = ?
       OR ls.rank = (SELECT rank FROM user_rank) - 1
    ORDER BY ls.rank;
""";

    public LeaderboardOverviewDto getLeaderboardWithMyPosition(String userId) {
        List<LeaderboardEntryDto> entries = jdbcTemplate.query(
                GET_LEADERBOARD_FROM_SNAPSHOT_SQL,
                (rs, rowNum) -> new LeaderboardEntryDto(
                        rs.getString("user_id"),
                        rs.getInt("rank"),
                        rs.getString("username"),
                        rs.getLong("total_points"),
                        rs.getInt("finished_decks_count")
                ),
                userId,
                userId,
                userId
        );

        List<LeaderboardEntryDto> top3 = entries.stream()
                .filter(e -> e.rank() <= 3)
                .toList();

        LeaderboardEntryDto myPosition = entries.stream()
                .filter(e -> e.userId().equals(userId))
                .findFirst()
                .orElse(
                        new LeaderboardEntryDto(
                                userId,
                                -1,
                                "You",
                                0L,
                                0
                        )
                );
        LeaderboardEntryDto aboveYou = entries.stream()
                .filter(e -> e.rank() == myPosition.rank() - 1)
                .findFirst()
                .orElse(
                        new LeaderboardEntryDto(
                                "",
                                -1,
                                "No one above you",
                                0L,
                                0
                        )
                );


        return new LeaderboardOverviewDto(top3, myPosition, aboveYou);
    }

}
