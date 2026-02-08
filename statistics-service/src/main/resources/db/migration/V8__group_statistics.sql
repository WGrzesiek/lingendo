-- Grupy
CREATE TABLE analytics.groups
(
    event_time  DateTime64(3, 'UTC'),
    group_id    String,
    group_name  String,
    teacher_id  String,
    status      LowCardinality(String) DEFAULT 'ACTIVE'
)
ENGINE = ReplacingMergeTree(event_time)
ORDER BY (teacher_id, group_id);

-- Członkowie grup
CREATE TABLE analytics.group_members
(
    event_time  DateTime64(3, 'UTC'),
    group_id    String,
    student_id  String,
    teacher_id  String,
    status      LowCardinality(String) DEFAULT 'ACTIVE'
)
ENGINE = ReplacingMergeTree(event_time)
ORDER BY (group_id, student_id);

-- Kursy udostępnione grupom
CREATE TABLE analytics.group_shared_decks
(
    event_time  DateTime64(3, 'UTC'),
    group_id    String,
    deck_id     String,
    deck_name   String,
    teacher_id  String
)
ENGINE = ReplacingMergeTree(event_time)
ORDER BY (group_id, deck_id);

-- Feed aktywności w grupie
CREATE TABLE analytics.group_activity
(
    event_time    DateTime64(3, 'UTC'),
    group_id      String,
    teacher_id    String,
    student_id    String,
    student_name  String,
    activity_type LowCardinality(String),
    deck_id       String,
    deck_name     String
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(event_time)
ORDER BY (group_id, event_time);

-- Leaderboard grupy (punkty dziennie)
CREATE TABLE analytics.group_leaderboard
(
    day        Date,
    group_id   String,
    student_id String,
    sessions   Int32,
    correct    Int32,
    total      Int32,
    points     Int64
)
ENGINE = SummingMergeTree
PARTITION BY toYYYYMM(day)
ORDER BY (group_id, day, student_id);

-- MV: Ukończone lekcje → feed grupy
CREATE MATERIALIZED VIEW analytics.mv_group_activity_sessions TO analytics.group_activity
AS SELECT
    sf.event_time,
    gm.group_id,
    gm.teacher_id,
    sf.user_id AS student_id,
    dictGet('analytics.usernames_dict', 'username', sf.user_id) AS student_name,
    'LESSON_COMPLETED' AS activity_type,
    sf.deck_id,
    sf.deck_name
FROM analytics.sessions_finished sf
INNER JOIN analytics.group_members gm ON sf.user_id = gm.student_id
WHERE gm.status = 'ACTIVE';

-- MV: Rozpoczęte kursy → feed grupy
CREATE MATERIALIZED VIEW analytics.mv_group_activity_enrollments TO analytics.group_activity
AS SELECT
    dec.event_time,
    gm.group_id,
    gm.teacher_id,
    dec.user_id AS student_id,
    dictGet('analytics.usernames_dict', 'username', dec.user_id) AS student_name,
    'COURSE_STARTED' AS activity_type,
    dec.deck_id,
    dec.deck_name
FROM analytics.deck_enrollments_created dec
INNER JOIN analytics.group_members gm ON dec.user_id = gm.student_id
WHERE gm.status = 'ACTIVE';

-- MV: Ukończone kursy → feed grupy
CREATE MATERIALIZED VIEW analytics.mv_group_activity_completions TO analytics.group_activity
AS SELECT
    def.event_time,
    gm.group_id,
    gm.teacher_id,
    def.user_id AS student_id,
    dictGet('analytics.usernames_dict', 'username', def.user_id) AS student_name,
    'COURSE_COMPLETED' AS activity_type,
    def.deck_id,
    def.deck_name
FROM analytics.deck_enrollments_finished def
INNER JOIN analytics.group_members gm ON def.user_id = gm.student_id
WHERE gm.status = 'ACTIVE';

-- MV: Leaderboard grupy z sesji
CREATE MATERIALIZED VIEW analytics.mv_group_leaderboard TO analytics.group_leaderboard
AS SELECT
    toDate(sf.event_time) AS day,
    gm.group_id,
    sf.user_id AS student_id,
    1 AS sessions,
    sf.correct_answers AS correct,
    sf.correct_answers + sf.incorrect_answers AS total,
    sf.correct_answers * 5 AS points
FROM analytics.sessions_finished sf
INNER JOIN analytics.group_members gm ON sf.user_id = gm.student_id
WHERE gm.status = 'ACTIVE';
