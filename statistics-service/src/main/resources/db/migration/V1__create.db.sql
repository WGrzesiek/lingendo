-- -- Baza (dla pewności, mimo CLICKHOUSE_DB)
-- CREATE DATABASE IF NOT EXISTS learnwords;
-- USE learnwords;
--
-- -- 1) Surowy log zdarzeń (append-only)
-- CREATE TABLE IF NOT EXISTS events_raw
-- (
--     event_time   DateTime,
--     user_id      String,
--     teacher_id   String,
--     event_type   LowCardinality(String),  -- 'lesson_completed', 'session_started', ...
--     duration_s   UInt32 DEFAULT 0,        -- opcjonalne
--     meta         JSON                     -- np. {"lessonId":"L-123","level":"A2"}
-- )
--     ENGINE = MergeTree
-- PARTITION BY toYYYYMM(event_time)
-- ORDER BY (event_type, event_time, user_id, teacher_id);
--
-- -- 2) Dzienne agregaty per user/teacher
-- -- Używamy AggregatingMergeTree + SimpleAggregateFunction (sum) → potem sumMerge()
-- CREATE TABLE IF NOT EXISTS agg_daily
-- (
--     day                Date,
--     teacher_id         String,
--     user_id            String,
--     lessons_completed  SimpleAggregateFunction(sum, UInt64),
--     sessions_started   SimpleAggregateFunction(sum, UInt64),
--     total_duration_s   SimpleAggregateFunction(sum, UInt64)
-- )
--     ENGINE = AggregatingMergeTree
-- PARTITION BY toYYYYMM(day)
-- ORDER BY (day, teacher_id, user_id);
--
-- -- 3) Widok materializowany: z events_raw do agg_daily
-- CREATE MATERIALIZED VIEW IF NOT EXISTS mv_events_to_daily
--             TO agg_daily
-- AS
-- SELECT
--     toDate(event_time)                                        AS day,
--     teacher_id,
--     user_id,
--     sum(event_type = 'lesson_completed')                      AS lessons_completed,
--     sum(event_type = 'session_started')                       AS sessions_started,
--     sumIf(duration_s, event_type = 'lesson_completed')        AS total_duration_s
-- FROM events_raw
-- GROUP BY day, teacher_id, user_id;
CREATE DATABASE IF NOT EXISTS analytics;

CREATE TABLE IF NOT EXISTS analytics.user_logins
(
    event_time   DateTime64(3, 'UTC'),
    user_id      String,
    username     String,
    email        String,
    received_at  DateTime64(3, 'UTC')
)
    ENGINE = MergeTree
PARTITION BY toYYYYMM(event_time)
ORDER BY (user_id, event_time);
