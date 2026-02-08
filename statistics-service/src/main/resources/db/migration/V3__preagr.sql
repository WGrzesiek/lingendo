CREATE TABLE analytics.user_points_monthly
(
    month   Date,
    user_id String,
    points  Int64
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(month)
ORDER BY (month, user_id);


CREATE TABLE analytics.user_points_total
(
    user_id String,
    points  Int64
)
ENGINE = SummingMergeTree
ORDER BY user_id;

CREATE MATERIALIZED VIEW analytics.mv_user_points_monthly
TO analytics.user_points_monthly
AS
SELECT
    toStartOfMonth(day) AS month,
    user_id,
    sum(points)         AS points
FROM analytics.user_points_daily
GROUP BY
    month,
    user_id;

CREATE MATERIALIZED VIEW analytics.mv_user_points_total
TO analytics.user_points_total
AS
SELECT
    user_id,
    sum(points) AS points
FROM analytics.user_points_daily
GROUP BY user_id;
