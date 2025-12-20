package com.learnwords.statisticsservice.repository;

import com.learnwords.common.events.DeckCreatedEvent;
import com.learnwords.common.events.SessionFinishedEvent;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;

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
                event.eventTime(),
                event.deckId(),
                event.userId(),
                event.deckName(),
                event.deckCategory(),
                event.languageFrom(),
                event.languageTo(),
                event.receivedAt()
        );
    }

    private static final String GET_CREATED_DECKS_COUNT_BY_USER_SQL = """
        SELECT count(*) AS created_decks
        FROM analytics.decks_created
        WHERE user_id = ?
        """;

    public int getCreatedDecksCountByUser(String userId) {
        Integer result = jdbcTemplate.queryForObject(
                GET_CREATED_DECKS_COUNT_BY_USER_SQL,
                (rs, rowNum) -> rs.getInt("created_decks"),
                userId
        );
        return result != null ? result : 0;
    }
}

