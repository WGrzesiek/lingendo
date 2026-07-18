-- statistics-service: schemat analityki na PostgreSQL (Neon).
-- Port z ClickHouse: tabele bazowe (eventy, append-only) + tabele wymiarowe/relacyjne (upsert),
-- warstwa pochodna (dawne materialized views ClickHouse) jako VIEWs (agregacja-on-read).
-- Typy: DateTime64->timestamptz, String->text, UInt/Int->int/bigint, LowCardinality->text.
CREATE SCHEMA IF NOT EXISTS analytics;

-- ========================= TABELE BAZOWE (eventy, append-only) =========================
CREATE TABLE analytics.user_logins (
    event_time  timestamptz NOT NULL,
    user_id     text        NOT NULL,
    username    text,
    streak      int,
    received_at timestamptz
);
CREATE INDEX ix_user_logins_user_time ON analytics.user_logins (user_id, event_time);

CREATE TABLE analytics.decks_created (
    event_time    timestamptz NOT NULL,
    deck_id       text NOT NULL,
    user_id       text NOT NULL,
    deck_name     text,
    deck_category text,
    language_from text,
    language_to   text,
    received_at   timestamptz
);
CREATE INDEX ix_decks_created_user_time ON analytics.decks_created (user_id, event_time);

CREATE TABLE analytics.deck_enrollments_created (
    event_time         timestamptz NOT NULL,
    deck_enrollment_id text NOT NULL,
    deck_id            text,
    deck_name          text,
    user_id            text NOT NULL,
    received_at        timestamptz
);
CREATE INDEX ix_dec_user_time ON analytics.deck_enrollments_created (user_id, event_time);

CREATE TABLE analytics.deck_enrollments_finished (
    event_time         timestamptz NOT NULL,
    deck_enrollment_id text NOT NULL,
    deck_id            text,
    deck_name          text,
    user_id            text NOT NULL,
    received_at        timestamptz
);
CREATE INDEX ix_def_user_time ON analytics.deck_enrollments_finished (user_id, event_time);

CREATE TABLE analytics.flashcards_created (
    event_time   timestamptz NOT NULL,
    flashcard_id text NOT NULL,
    deck_id      text,
    user_id      text,
    received_at  timestamptz
);
CREATE INDEX ix_flashcards_created_deck_time ON analytics.flashcards_created (deck_id, event_time);

CREATE TABLE analytics.sessions_started (
    event_time         timestamptz NOT NULL,
    session_id         text NOT NULL,
    user_id            text NOT NULL,
    deck_id            text,
    deck_name          text,
    deck_enrollment_id text,
    received_at        timestamptz
);
CREATE INDEX ix_sessions_started_user_time ON analytics.sessions_started (user_id, event_time);

CREATE TABLE analytics.sessions_finished (
    event_time         timestamptz NOT NULL,
    started_at         timestamptz,
    session_id         text NOT NULL,
    user_id            text NOT NULL,
    deck_id            text,
    deck_name          text,
    deck_enrollment_id text,
    correct_answers    int DEFAULT 0,
    incorrect_answers  int DEFAULT 0,
    received_at        timestamptz
);
CREATE INDEX ix_sessions_finished_user_time ON analytics.sessions_finished (user_id, event_time);

CREATE TABLE analytics.flashcard_answers (
    event_time         timestamptz NOT NULL,
    user_id            text,
    deck_enrollment_id text,
    session_id         text,
    flashcard_id       text,
    correct            smallint DEFAULT 0,
    received_at        timestamptz,
    time_taken_ms      int
);
CREATE INDEX ix_flashcard_answers_enr_sess ON analytics.flashcard_answers (deck_enrollment_id, session_id, event_time);
CREATE INDEX ix_flashcard_answers_user_time ON analytics.flashcard_answers (user_id, event_time);

-- ========================= TABELE WYMIAROWE / RELACYJNE (upsert: INSERT ... ON CONFLICT) =========================
-- Dawne ReplacingMergeTree(event_time): PK = klucz sortowania, consumer robi upsert.
CREATE TABLE analytics.user_dim (
    user_id    text PRIMARY KEY,
    username   text,
    event_time timestamptz DEFAULT now()
);

CREATE TABLE analytics.teacher_students (
    event_time timestamptz NOT NULL,
    teacher_id text NOT NULL,
    student_id text NOT NULL,
    status     text DEFAULT 'ACTIVE',
    PRIMARY KEY (teacher_id, student_id)
);

CREATE TABLE analytics.teacher_shared_decks (
    event_time timestamptz NOT NULL,
    teacher_id text NOT NULL,
    deck_id    text NOT NULL,
    deck_name  text,
    PRIMARY KEY (teacher_id, deck_id)
);

CREATE TABLE analytics.user_friendships (
    event_time timestamptz NOT NULL,
    user_id    text NOT NULL,
    friend_id  text NOT NULL,
    status     text DEFAULT 'ACTIVE',
    PRIMARY KEY (user_id, friend_id)
);

CREATE TABLE analytics.groups (
    event_time timestamptz NOT NULL,
    group_id   text PRIMARY KEY,
    group_name text,
    teacher_id text,
    status     text DEFAULT 'ACTIVE'
);

CREATE TABLE analytics.group_members (
    event_time timestamptz NOT NULL,
    group_id   text NOT NULL,
    student_id text NOT NULL,
    teacher_id text,
    status     text DEFAULT 'ACTIVE',
    PRIMARY KEY (group_id, student_id)
);

CREATE TABLE analytics.group_shared_decks (
    event_time timestamptz NOT NULL,
    group_id   text NOT NULL,
    deck_id    text NOT NULL,
    deck_name  text,
    teacher_id text,
    PRIMARY KEY (group_id, deck_id)
);

-- ========================= WARSTWA POCHODNA: VIEWs (dawne materialized views CH) =========================
-- Feed aktywności użytkownika (dawne mv_*_feed → user_activity).
CREATE VIEW analytics.user_activity AS
    SELECT event_time, user_id, 'LESSON_COMPLETED'::text AS type,
           'Ukończono lekcję'::text AS title, deck_name AS subtitle, 50 AS points
    FROM analytics.sessions_finished
    UNION ALL
    SELECT event_time, user_id, 'SESSION_STARTED', 'Rozpoczęto nowy kurs', deck_name, 0
    FROM analytics.deck_enrollments_created
    UNION ALL
    SELECT event_time, user_id, 'SESSION_COMPLETED', 'Ukończono kurs', deck_name, 100
    FROM analytics.deck_enrollments_finished
    UNION ALL
    SELECT event_time, user_id, 'LOGIN', 'Seria dni nauki!',
           streak || ' dni nauki z rzędu – nowy rekord!', 10
    FROM analytics.user_logins;

-- Punkty dzienne (dawne mv_user_points_daily z flashcard_answers + mv_points_daily z user_activity).
CREATE VIEW analytics.user_points_daily AS
    SELECT day, user_id, sum(points)::bigint AS points
    FROM (
        SELECT (event_time AT TIME ZONE 'UTC')::date AS day, user_id, (correct * 5) AS points
        FROM analytics.flashcard_answers
        UNION ALL
        SELECT (event_time AT TIME ZONE 'UTC')::date AS day, user_id, points
        FROM analytics.user_activity
    ) src
    GROUP BY day, user_id;

CREATE VIEW analytics.user_points_monthly AS
    SELECT date_trunc('month', day)::date AS month, user_id, sum(points)::bigint AS points
    FROM analytics.user_points_daily
    GROUP BY month, user_id;

CREATE VIEW analytics.user_points_total AS
    SELECT user_id, sum(points)::bigint AS points
    FROM analytics.user_points_daily
    GROUP BY user_id;

-- Feed aktywności uczniów nauczyciela (dawne mv_teacher_activity_* + dictGet → JOIN user_dim).
CREATE VIEW analytics.teacher_student_activity AS
    SELECT sf.event_time, ts.teacher_id, sf.user_id AS student_id,
           ud.username AS student_name, 'LESSON_COMPLETED'::text AS activity_type,
           sf.deck_id, sf.deck_name
    FROM analytics.sessions_finished sf
    JOIN analytics.teacher_students ts ON sf.user_id = ts.student_id AND ts.status = 'ACTIVE'
    LEFT JOIN analytics.user_dim ud ON ud.user_id = sf.user_id
    UNION ALL
    SELECT dec.event_time, ts.teacher_id, dec.user_id, ud.username, 'COURSE_STARTED',
           dec.deck_id, dec.deck_name
    FROM analytics.deck_enrollments_created dec
    JOIN analytics.teacher_students ts ON dec.user_id = ts.student_id AND ts.status = 'ACTIVE'
    LEFT JOIN analytics.user_dim ud ON ud.user_id = dec.user_id
    UNION ALL
    SELECT def.event_time, ts.teacher_id, def.user_id, ud.username, 'COURSE_COMPLETED',
           def.deck_id, def.deck_name
    FROM analytics.deck_enrollments_finished def
    JOIN analytics.teacher_students ts ON def.user_id = ts.student_id AND ts.status = 'ACTIVE'
    LEFT JOIN analytics.user_dim ud ON ud.user_id = def.user_id;

-- Statystyki znajomych dziennie (dawne mv_friends_stats).
CREATE VIEW analytics.friends_stats_daily AS
    SELECT (sf.event_time AT TIME ZONE 'UTC')::date AS day, uf.user_id, uf.friend_id,
           count(*)::int AS sessions,
           sum(sf.correct_answers)::int AS correct,
           sum(sf.correct_answers + sf.incorrect_answers)::int AS total,
           sum(sf.correct_answers * 5)::bigint AS points
    FROM analytics.sessions_finished sf
    JOIN analytics.user_friendships uf ON sf.user_id = uf.friend_id AND uf.status = 'ACTIVE'
    GROUP BY day, uf.user_id, uf.friend_id;

-- Feed aktywności w grupie (dawne mv_group_activity_*).
CREATE VIEW analytics.group_activity AS
    SELECT sf.event_time, gm.group_id, gm.teacher_id, sf.user_id AS student_id,
           ud.username AS student_name, 'LESSON_COMPLETED'::text AS activity_type,
           sf.deck_id, sf.deck_name
    FROM analytics.sessions_finished sf
    JOIN analytics.group_members gm ON sf.user_id = gm.student_id AND gm.status = 'ACTIVE'
    LEFT JOIN analytics.user_dim ud ON ud.user_id = sf.user_id
    UNION ALL
    SELECT dec.event_time, gm.group_id, gm.teacher_id, dec.user_id, ud.username, 'COURSE_STARTED',
           dec.deck_id, dec.deck_name
    FROM analytics.deck_enrollments_created dec
    JOIN analytics.group_members gm ON dec.user_id = gm.student_id AND gm.status = 'ACTIVE'
    LEFT JOIN analytics.user_dim ud ON ud.user_id = dec.user_id
    UNION ALL
    SELECT def.event_time, gm.group_id, gm.teacher_id, def.user_id, ud.username, 'COURSE_COMPLETED',
           def.deck_id, def.deck_name
    FROM analytics.deck_enrollments_finished def
    JOIN analytics.group_members gm ON def.user_id = gm.student_id AND gm.status = 'ACTIVE'
    LEFT JOIN analytics.user_dim ud ON ud.user_id = def.user_id;

-- Leaderboard grupy dziennie (dawne mv_group_leaderboard).
CREATE VIEW analytics.group_leaderboard AS
    SELECT (sf.event_time AT TIME ZONE 'UTC')::date AS day, gm.group_id, sf.user_id AS student_id,
           count(*)::int AS sessions,
           sum(sf.correct_answers)::int AS correct,
           sum(sf.correct_answers + sf.incorrect_answers)::int AS total,
           sum(sf.correct_answers * 5)::bigint AS points
    FROM analytics.sessions_finished sf
    JOIN analytics.group_members gm ON sf.user_id = gm.student_id AND gm.status = 'ACTIVE'
    GROUP BY day, gm.group_id, sf.user_id;

-- Leaderboard globalny (dawny leaderboard_snapshot z crona) — teraz rank liczony on-read.
CREATE VIEW analytics.leaderboard_snapshot AS
    SELECT row_number() OVER (ORDER BY pt.points DESC) AS rank,
           pt.user_id, ud.username, pt.points AS total_points,
           COALESCE(fd.finished_decks_count, 0) AS finished_decks_count
    FROM analytics.user_points_total pt
    LEFT JOIN analytics.user_dim ud ON ud.user_id = pt.user_id
    LEFT JOIN (
        SELECT user_id, count(DISTINCT deck_id)::int AS finished_decks_count
        FROM analytics.deck_enrollments_finished
        GROUP BY user_id
    ) fd ON fd.user_id = pt.user_id;
