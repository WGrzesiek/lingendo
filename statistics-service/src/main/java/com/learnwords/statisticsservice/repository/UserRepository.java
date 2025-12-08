package com.learnwords.statisticsservice.repository;


import com.learnwords.common.events.UserLoginEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

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
}