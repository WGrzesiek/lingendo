-- V2__migracja.sql

-- 1) DECK:
ALTER TABLE deck ADD COLUMN owner varchar(255) NOT NULL DEFAULT 'I';
ALTER TABLE deck ADD CONSTRAINT deck_owner_check CHECK (owner IN ('I', 'TEACHER', 'FRIEND', 'COMMUNITY'));

-- 2) SESSION:
ALTER TABLE session ALTER COLUMN id TYPE varchar(36);

ALTER TABLE session
    ADD COLUMN user_id varchar(36) NOT NULL DEFAULT 'UNKNOWN',
    ADD COLUMN total_flashcards integer NOT NULL DEFAULT 0,
    ADD COLUMN correct_answers integer NOT NULL DEFAULT 0,
    ADD COLUMN wrong_answers integer NOT NULL DEFAULT 0,
    ADD COLUMN skipped integer NOT NULL DEFAULT 0,
    ADD COLUMN duration_seconds bigint,
    ADD COLUMN completed_at timestamp(6) with time zone,
    ADD COLUMN status varchar(255) NOT NULL DEFAULT 'IN_PROGRESS',
    ADD COLUMN type varchar(255) NOT NULL DEFAULT 'STANDARD';
ALTER TABLE session ADD CONSTRAINT session_status_check CHECK (status IN ('IN_PROGRESS', 'COMPLETED','PAUSED' , 'ABANDONED'));
ALTER TABLE session ADD CONSTRAINT session_type_check CHECK (type IN ('LEARNING', 'REVIEW'));

