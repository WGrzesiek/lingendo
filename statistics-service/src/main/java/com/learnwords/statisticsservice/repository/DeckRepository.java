package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.DeckCreatedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Repository
public class DeckRepository {

    private final JdbcTemplate jdbcTemplate;

    public DeckRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private static final String INSERT_DECK_CREATED_SQL = """
        INSERT INTO analytics.decks_created
            (event_time, deck_id, user_id, deck_name, deck_category, language_from, language_to, received_at)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;


    public void saveDeckCreated(DeckCreatedEvent event) {
        jdbcTemplate.update(
                INSERT_DECK_CREATED_SQL,
                java.sql.Timestamp.from(event.eventTime()),
                event.deckId(),
                event.userId(),
                event.deckName(),
                event.deckCategory(),
                event.languageFrom(),
                event.languageTo(),
                java.sql.Timestamp.from(event.receivedAt())
        );
    }

    private static final String GET_CREATED_DECKS_COUNT_BY_USER_SQL = """
        SELECT count(*) AS created_decks
        FROM analytics.decks_created
        WHERE user_id = ?
        """;

    private static final String GET_CREATED_DECKS_COUNT_BY_USER_SQL_WITH_DATE = """
        SELECT count(*) AS created_decks
        FROM analytics.decks_created
        WHERE user_id = ? AND event_time >= ?
        """;

    public int getCreatedDecksCountByUser(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_CREATED_DECKS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getInt("created_decks"),
                userId
        );
        return result != null ? result : 0;
    }

    public int getCreatedDecksCountByUser(String userId, Integer lastDays) {
        if (lastDays == null || lastDays <= 0) {
            return getCreatedDecksCountByUser(userId);
        }
        Instant threshold = Instant.now().minus(lastDays.longValue(), ChronoUnit.DAYS);
        Timestamp ts = Timestamp.from(threshold);
        Integer result = jdbcTemplate.queryForObject(
                GET_CREATED_DECKS_COUNT_BY_USER_SQL_WITH_DATE,
                (rs, rowNum) -> rs.getInt("created_decks"),
                userId,
                ts
        );
        return result != null ? result : 0;
    }
}

