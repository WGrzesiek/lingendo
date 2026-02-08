CREATE TABLE session (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    enrollment_id VARCHAR(36) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'IN_PROGRESS'
        CONSTRAINT session_status_check
            CHECK (status IN ('IN_PROGRESS', 'COMPLETED', 'PAUSED', 'ABANDONED')),
    type VARCHAR(20) NOT NULL
        CONSTRAINT session_type_check
            CHECK (type IN ('LEARNING', 'REVIEW')),
    started_at TIMESTAMP(6) WITH TIME ZONE,
    completed_at TIMESTAMP(6) WITH TIME ZONE,
    session_number INTEGER,
    correct_answers INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_session_enrollment
        FOREIGN KEY (enrollment_id)
        REFERENCES deck_enrollment(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_session_enrollment_id ON session(enrollment_id);
CREATE INDEX idx_session_status ON session(status);
CREATE INDEX idx_session_type ON session(type);
CREATE INDEX idx_session_started_at ON session(started_at);

CREATE TABLE session_flashcard (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    session_id VARCHAR(36) NOT NULL,
    flashcard_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_session_flashcard_session
        FOREIGN KEY (session_id)
        REFERENCES session(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_session_flashcard_flashcard
        FOREIGN KEY (flashcard_id)
        REFERENCES flashcard(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_session_flashcard_session_id ON session_flashcard(session_id);
CREATE INDEX idx_session_flashcard_flashcard_id ON session_flashcard(flashcard_id);
