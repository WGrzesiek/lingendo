CREATE TABLE analytics.leaderboard_snapshot
(
    snapshot_time DateTime DEFAULT now(),
    rank Int32,
    user_id String,
    username String,
    total_points Int64,
    finished_decks_count Int32
)
    ENGINE = ReplacingMergeTree(snapshot_time)
PARTITION BY toYYYYMMDD(snapshot_time)
ORDER BY (snapshot_time, rank);


-- cron na 192.168.23.9
-- crontab -e
-- */5 * * * * /usr/bin/docker exec -i clickhouse clickhouse-client --multiquery < /home/wawrzen/leaderboard_snapshot.sql >> /home/wawrzen/leaderboard_cron.log 2>&1