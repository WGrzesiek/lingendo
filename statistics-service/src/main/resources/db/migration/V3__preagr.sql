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

--=================
--TOP 10 miesiac
WITH toStartOfMonth(today()) AS cur_month
SELECT
    user_id,
    points,
    dense_rank() OVER (ORDER BY points DESC) AS rank
FROM analytics.user_points_monthly
WHERE month = cur_month
ORDER BY rank
LIMIT 10;

--TOP 10 all
SELECT
    user_id,
    points,
    dense_rank() OVER (ORDER BY points DESC) AS rank
FROM analytics.user_points_total
ORDER BY rank
LIMIT 10;

--porownanie z poprzednim msc
WITH
    toStartOfMonth(today())      AS cur_month,
    addMonths(cur_month, -1)     AS prev_month
SELECT
    cur.user_id,

    cur.points                   AS points_current,
    cur.rank                     AS rank_current,

    prev.points                  AS points_previous,
    prev.rank                    AS rank_previous,

    (cur.points - coalesce(prev.points, 0))        AS points_diff,
    (coalesce(prev.rank, 100000) - cur.rank)       AS rank_change
FROM
(
    SELECT
        user_id,
        points,
        dense_rank() OVER (ORDER BY points DESC) AS rank
    FROM analytics.user_points_monthly
    WHERE month = cur_month
) cur
LEFT JOIN
(
    SELECT
        user_id,
        points,
        dense_rank() OVER (ORDER BY points DESC) AS rank
    FROM analytics.user_points_monthly
    WHERE month = prev_month
) prev USING (user_id)
ORDER BY rank_current
LIMIT 10;
