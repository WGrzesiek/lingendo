CREATE TABLE analytics.user_dim
(
    user_id   String,
    username  String,
    event_time DateTime DEFAULT now()
)
    ENGINE = ReplacingMergeTree(event_time)
ORDER BY user_id;

CREATE MATERIALIZED VIEW analytics.mv_fill_user_dim
            TO analytics.user_dim
AS
SELECT
    user_id,
    username,
    event_time
FROM analytics.user_logins;

CREATE DICTIONARY analytics.usernames_dict
(
    user_id  String,
    username String
)
    PRIMARY KEY user_id
    SOURCE(CLICKHOUSE(
            host 'clickhouse'
            port 9000 -- musi byc natywny port ClickHouse a nie HTTP
            user 'wawrzen'
            password 'Ubuntu98'
            db   'analytics'
            table 'user_dim'
           ))
    LAYOUT(HASHED())
    LIFETIME(MIN 60 MAX 300);
