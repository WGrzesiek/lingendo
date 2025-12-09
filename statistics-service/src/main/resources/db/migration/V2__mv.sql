CREATE MATERIALIZED VIEW analytics.mv_session_completed_feed TO analytics.user_activity
AS
SELECT
    event_time,
    user_id,
    'LESSON_COMPLETED'                   AS type,
    'Ukończono lekcję'                   AS title,
    deck_name                            AS subtitle,
    50                                   AS points
FROM analytics.sessions_finished;

CREATE MATERIALIZED VIEW analytics.mv_deck_enrollments_started_feed TO analytics.user_activity
AS
SELECT
    event_time,
    user_id,
    'SESSION_STARTED' AS type,
    'Rozpoczęto nowy kurs' AS title,
    deck_name                            AS subtitle,
    0 AS points
FROM analytics.deck_enrollments_created;

CREATE MATERIALIZED VIEW analytics.mv_deck_enrollments_completed_feed TO analytics.user_activity
AS
SELECT
    event_time,
    user_id,
    'SESSION_COMPLETED' AS type,
    'Ukończono kurs' AS title,
    deck_name                            AS subtitle,
    100 AS points
FROM analytics.deck_enrollments_finished;

CREATE MATERIALIZED VIEW analytics.mv_login_feed TO analytics.user_activity
AS
SELECT
    event_time,
    user_id,
    'LOGIN' AS type,
    concat(toString(streak), ' dni nauki z rzędu – nowy rekord!') AS title,
    10 AS points
FROM analytics.user_logins;

CREATE MATERIALIZED VIEW analytics.mv_points_daily
            TO analytics.user_points_daily
AS
SELECT
    toDate(event_time) AS day,
    user_id,
    sum(points) AS points
FROM analytics.user_activity
GROUP BY
    user_id,
    day;


