-- Relacja nauczyciel-uczeń
CREATE TABLE analytics.teacher_students
(
    event_time  DateTime64(3, 'UTC'),
    teacher_id  String,
    student_id  String,
    status      LowCardinality(String) DEFAULT 'ACTIVE'
)
ENGINE = ReplacingMergeTree(event_time)
ORDER BY (teacher_id, student_id);

-- Kursy udostępnione przez nauczyciela
CREATE TABLE analytics.teacher_shared_decks
(
    event_time  DateTime64(3, 'UTC'),
    teacher_id  String,
    deck_id     String,
    deck_name   String
)
ENGINE = ReplacingMergeTree(event_time)
ORDER BY (teacher_id, deck_id);

-- Aktywność uczniów nauczyciela (feed)
CREATE TABLE analytics.teacher_student_activity
(
    event_time    DateTime64(3, 'UTC'),
    teacher_id    String,
    student_id    String,
    student_name  String,
    activity_type LowCardinality(String),
    deck_id       String,
    deck_name     String
)
ENGINE = MergeTree
PARTITION BY toYYYYMM(event_time)
ORDER BY (teacher_id, event_time);

-- MV: Ukończone lekcje uczniów → feed nauczyciela
CREATE MATERIALIZED VIEW analytics.mv_teacher_activity_sessions TO analytics.teacher_student_activity
AS SELECT
    sf.event_time,
    ts.teacher_id,
    sf.user_id AS student_id,
    dictGet('analytics.usernames_dict', 'username', sf.user_id) AS student_name,
    'LESSON_COMPLETED' AS activity_type,
    sf.deck_id,
    sf.deck_name
FROM analytics.sessions_finished sf
INNER JOIN analytics.teacher_students ts ON sf.user_id = ts.student_id
WHERE ts.status = 'ACTIVE';

-- MV: Rozpoczęte kursy → feed nauczyciela
CREATE MATERIALIZED VIEW analytics.mv_teacher_activity_enrollments TO analytics.teacher_student_activity
AS SELECT
    dec.event_time,
    ts.teacher_id,
    dec.user_id AS student_id,
    dictGet('analytics.usernames_dict', 'username', dec.user_id) AS student_name,
    'COURSE_STARTED' AS activity_type,
    dec.deck_id,
    dec.deck_name
FROM analytics.deck_enrollments_created dec
INNER JOIN analytics.teacher_students ts ON dec.user_id = ts.student_id
WHERE ts.status = 'ACTIVE';

-- MV: Ukończone kursy → feed nauczyciela
CREATE MATERIALIZED VIEW analytics.mv_teacher_activity_completed TO analytics.teacher_student_activity
AS SELECT
    def.event_time,
    ts.teacher_id,
    def.user_id AS student_id,
    dictGet('analytics.usernames_dict', 'username', def.user_id) AS student_name,
    'COURSE_COMPLETED' AS activity_type,
    def.deck_id,
    def.deck_name
FROM analytics.deck_enrollments_finished def
INNER JOIN analytics.teacher_students ts ON def.user_id = ts.student_id
WHERE ts.status = 'ACTIVE';
