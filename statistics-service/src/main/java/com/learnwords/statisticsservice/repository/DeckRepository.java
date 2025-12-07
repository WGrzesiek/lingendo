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
}

