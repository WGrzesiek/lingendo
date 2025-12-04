package com.learnwords.statisticsservice.repository;


import com.learnwords.common.events.UserLoginEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;

@Repository
public class UserLoginStatsRepository {

    private final JdbcTemplate jdbcTemplate;

    public UserLoginStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_SQL = """
        INSERT INTO analytics.user_logins
            (event_time, user_id, username, email,received_at)
        VALUES (?, ?, ?, ?, ?)
        """;

    public void save(UserLoginEvent event) {
        Instant now = Instant.now();

        jdbcTemplate.update(
                INSERT_SQL,
                Timestamp.from(event.occurredAt()),
                event.eventId(),
                event.userId(),
                event.username(),
                event.email(),
                Timestamp.from(now)
        );
    }
}