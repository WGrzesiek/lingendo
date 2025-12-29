package com.learnwords.statisticsservice.repository;


import com.learnwords.common.events.UserLoginEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.LinkedHashMap;
import java.util.Map;

@Repository
public class UserRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = """
        INSERT INTO analytics.user_logins
            (event_time, user_id, username, streak, received_at)
        VALUES (?, ?, ?, ?, ?)
        """;

    public void save(UserLoginEvent event) {
        jdbcTemplate.update(
                INSERT_SQL,
                event.eventTime(),
                event.userId(),
                event.username(),
                event.streak(),
                event.received_at()
        );
    }

    private static final String get_user_streak_SQL = """
        SELECT streak AS user_streak
        FROM analytics.user_logins
        WHERE user_id = ?
        ORDER BY event_time DESC
        LIMIT 1
        """;

    public Integer getUserStreak(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                get_user_streak_SQL,
                (rs, rowNum) -> rs.getInt("user_streak"),
                userId
        );
        return result != null ? result : 0;
    }

    private static final String GET_TOTAL_POINTS_SQL = """
        SELECT sum(points) AS total_points
        FROM analytics.user_points_total
        WHERE user_id = ?
        """;

    public Long getTotalPoints(String userId) {
        Long result = jdbcTemplate.queryForObject(
                GET_TOTAL_POINTS_SQL,
                ((rs, rowNum) -> rs.getLong("total_points")),
                userId
        );
        return result != null ? result : 0L;
    }

    private static final String GET_POINTS_PER_MONTH_BY_USER = """
        SELECT toYYYYMM(month) AS month, sum(points) AS points
        FROM analytics.user_points_monthly
        WHERE user_id = ?
        GROUP BY month
        ORDER BY month DESC
    """;

    public Map<String, Long> getPointsPerMonth(String userId) {
        return jdbcTemplate.query(
                GET_POINTS_PER_MONTH_BY_USER,
                rs -> {
                    Map<String, Long> result = new LinkedHashMap<>();
                    while (rs.next()) {
                        result.put(
                                rs.getString("month"),
                                rs.getLong("points")
                        );
                    }
                    return result;
                },
                userId
        );
    }

    private static final String GET_POINTS_PER_DAY_BY_USER = """
        SELECT toYYYYMMDD(day) AS day, sum(points) AS points
        FROM analytics.user_points_daily
        WHERE user_id = ?
        GROUP BY day
        ORDER BY day DESC
    """;

    public Map<String, Long> getPointsPerDay(String userId) {
        return jdbcTemplate.query(
                GET_POINTS_PER_DAY_BY_USER,
                rs -> {
                    Map<String, Long> result = new LinkedHashMap<>();
                    while (rs.next()) {
                        result.put(
                                rs.getString("day"),
                                rs.getLong("points")
                        );
                    }
                    return result;
                },
                userId
        );
    }


}