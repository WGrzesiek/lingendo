-- V3__triger.sql

ALTER TABLE deck
    ADD COLUMN IF NOT EXISTS last_accessed timestamp(6) with time zone,
    ADD COLUMN IF NOT EXISTS total_session BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS session_completed BIGINT NOT NULL DEFAULT 0,
    ADD COLUMN IF NOT EXISTS difficulty VARCHAR(50) NOT NULL DEFAULT 'EASY',
    ADD COLUMN IF NOT EXISTS status VARCHAR(50) NOT NULL DEFAULT 'IN_PROGRESS';
ALTER TABLE deck ADD CONSTRAINT deck_difficulty_check CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD'));
ALTER TABLE deck ADD CONSTRAINT  deck_status_check CHECK (status IN ('IN_PROGRESS', 'COMPLETED','NOT_STARTED', 'ARCHIVED'));

-- =========================================================
-- Funkcja: ustawianie last_accessed przy zmianie liczników
-- =========================================================
CREATE OR REPLACE FUNCTION deck_set_last_accessed()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.last_accessed := now();
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_deck_set_last_accessed ON deck;

CREATE TRIGGER trg_deck_set_last_accessed
    BEFORE UPDATE OF total_session, session_completed ON deck
    FOR EACH ROW
    WHEN (
        OLD.total_session IS DISTINCT FROM NEW.total_session
            OR OLD.session_completed IS DISTINCT FROM NEW.session_completed
        )
EXECUTE FUNCTION deck_set_last_accessed();

-- =========================================================
-- Funkcja: przeliczanie statystyk sesji dla talii
-- =========================================================
CREATE OR REPLACE FUNCTION session_update_deck_stats()
    RETURNS TRIGGER AS $$
DECLARE
    v_deck_id uuid;
BEGIN
    IF (TG_OP = 'DELETE') THEN
        v_deck_id := OLD.deck_id;
    ELSE
        v_deck_id := NEW.deck_id;
    END IF;

    UPDATE deck d
    SET
        total_session = (
            SELECT COUNT(*)
            FROM session s
            WHERE s.deck_id = v_deck_id
        ),
        session_completed = (
            SELECT COUNT(*)
            FROM session s
            WHERE s.deck_id = v_deck_id
              AND s.status = 'COMPLETED'
        )
    WHERE d.id = v_deck_id;

    RETURN NULL;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS trg_session_update_deck_stats ON session;

CREATE TRIGGER trg_session_update_deck_stats
    AFTER INSERT OR UPDATE OF status, deck_id OR DELETE ON session
    FOR EACH ROW
EXECUTE FUNCTION session_update_deck_stats();
