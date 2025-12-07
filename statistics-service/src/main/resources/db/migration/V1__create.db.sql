CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE analytics.user_logins
(
    event_time   DateTime64(3, 'UTC'),
    user_id      String,
    username     String,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (user_id, event_time);

CREATE TABLE analytics.decks_created
(
    event_time  DateTime64(3, 'UTC'),
    deck_id     String,
    user_id     String,
    deck_name   String,
    deck_category LowCardinality(String),
    language_from LowCardinality(String),
    language_to   LowCardinality(String),
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (user_id, event_time);

CREATE TABLE analytics.deck_enrollments_created
(
    event_time  DateTime64(3, 'UTC'),
    deck_enrollment_id  String,
    deck_id     String,
    user_id     String,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (deck_id, event_time);

CREATE TABLE analytics.deck_enrollments_finished
(
    event_time          DateTime64(3, 'UTC'),
    deck_enrollment_id  String,
    deck_id             String,
    user_id             String,
    correct_answers     UInt32,
    incorrect_answers   UInt32,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (user_id, event_time);

CREATE TABLE analytics.flashcards_created
(
    event_time  DateTime64(3, 'UTC'),
    flashcard_id String,
    deck_id      String,
    user_id      String,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (deck_id, event_time);

CREATE TABLE analytics.sessions_started
(
    event_time  DateTime64(3, 'UTC'),
    session_id String,
    user_id    String,
    deck_id             String,
    deck_enrollment_id    String,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (user_id, event_time);

CREATE TABLE analytics.sessions_finished
(
    event_time          DateTime64(3, 'UTC'),
    started_at          DateTime64(3, 'UTC'),
    session_id          String,
    user_id             String,
    deck_id             String,
    deck_enrollment_id  String,
    correct_answers     UInt16,
    incorrect_answers   UInt16,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (user_id, event_time);

CREATE TABLE analytics.flashcard_answers
(
    event_time   DateTime64(3, 'UTC'),
    user_id      String,
    deck_enrollment_id      String,
    session_id   String,
    flashcard_id String,
    correct      UInt8,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
        PARTITION BY toYYYYMM(event_time)
        ORDER BY (deck_enrollment_id, session_id, event_time);

-- ===========================================
-- Punkty
CREATE TABLE analytics.user_points_daily
(
    day       Date,
    user_id   String,
    points    Int32
)
    ENGINE = SummingMergeTree
        PARTITION BY toYYYYMM(day)
        ORDER BY (user_id, day);

CREATE MATERIALIZED VIEW analytics.mv_user_points_daily
            TO analytics.user_points_daily
AS
SELECT
    toDate(event_time) AS day,
    user_id,
    sum(correct) * 5 AS points
FROM analytics.flashcard_answers
GROUP BY day, user_id;
