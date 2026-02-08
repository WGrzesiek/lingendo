-- Relacje znajomych
CREATE TABLE analytics.user_friendships
(
    event_time  DateTime64(3, 'UTC'),
    user_id     String,
    friend_id   String,
    status      LowCardinality(String) DEFAULT 'ACTIVE'
)
ENGINE = ReplacingMergeTree(event_time)
ORDER BY (user_id, friend_id);

-- Statystyki znajomych dziennie (do porównań i leaderboardu)
CREATE TABLE analytics.friends_stats_daily
(
    day       Date,
    user_id   String,
    friend_id String,
    sessions  Int32,
    correct   Int32,
    total     Int32,
    points    Int64
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(day)
ORDER BY (user_id, friend_id, day);

-- MV: Statystyki znajomych z sesji
CREATE MATERIALIZED VIEW analytics.mv_friends_stats TO analytics.friends_stats_daily
AS SELECT
    toDate(sf.event_time) AS day,
    uf.user_id,
    uf.friend_id,
    1 AS sessions,
    sf.correct_answers AS correct,
    sf.correct_answers + sf.incorrect_answers AS total,
    sf.correct_answers * 5 AS points
FROM analytics.sessions_finished sf
INNER JOIN analytics.user_friendships uf ON sf.user_id = uf.friend_id
WHERE uf.status = 'ACTIVE';
