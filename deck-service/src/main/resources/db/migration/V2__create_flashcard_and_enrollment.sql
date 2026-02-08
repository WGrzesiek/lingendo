CREATE TABLE flashcard (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    word_id VARCHAR(36) NOT NULL,
    deck_id VARCHAR(36) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_flashcard_deck
        FOREIGN KEY (deck_id)
        REFERENCES deck(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_flashcard_deck_id ON flashcard(deck_id);
CREATE INDEX idx_flashcard_word_id ON flashcard(word_id);

CREATE TABLE deck_enrollment (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    deck_id VARCHAR(36) NOT NULL,
    user_id VARCHAR(36) NOT NULL,
    role VARCHAR(20) NOT NULL
        CONSTRAINT deck_enrollment_role_check
            CHECK (role IN ('OWNER', 'STUDENT', 'VIEWER')),
    source VARCHAR(30)
        CONSTRAINT deck_enrollment_source_check
            CHECK (source IN ('CREATED', 'ENROLLED_PUBLIC', 'SHARED_BY_TEACHER', 'SHARED_BY_FRIEND', 'SHARED_BY_USER', 'SHARED_BY_GROUP')),
    how_many_flashcards_for_one_session BIGINT DEFAULT 20,
    preferred_algorithm VARCHAR(30)
        CONSTRAINT deck_enrollment_algorithm_check
            CHECK (preferred_algorithm IN ('GRZESIEK_ALGORITHM', 'LEINER_ALGORITHM', 'TEST_ALGORITHM')),
    preferred_review_schedule VARCHAR(20) NOT NULL DEFAULT 'AUTO'
        CONSTRAINT deck_enrollment_schedule_check
            CHECK (preferred_review_schedule IN ('AUTO', 'DAILY', 'EVERY_OTHER_DAY', 'WEEKLY', 'CUSTOM')),
    status VARCHAR(20) NOT NULL DEFAULT 'NOT_STARTED'
        CONSTRAINT deck_enrollment_status_check
            CHECK (status IN ('NOT_STARTED', 'IN_PROGRESS', 'COMPLETED', 'ARCHIVED')),
    joined_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    completed_sessions_count INTEGER NOT NULL DEFAULT 0,
    total_learning_time_seconds BIGINT NOT NULL DEFAULT 0,
    learned_flashcards_count INTEGER NOT NULL DEFAULT 0,
    last_accessed_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_deck_enrollment_deck
        FOREIGN KEY (deck_id)
        REFERENCES deck(id)
        ON DELETE CASCADE,
    CONSTRAINT uk_deck_enrollment_user_deck
        UNIQUE (deck_id, user_id)
);

CREATE INDEX idx_deck_enrollment_deck_id ON deck_enrollment(deck_id);
CREATE INDEX idx_deck_enrollment_user_id ON deck_enrollment(user_id);
CREATE INDEX idx_deck_enrollment_status ON deck_enrollment(status);
CREATE INDEX idx_deck_enrollment_role ON deck_enrollment(role);
CREATE INDEX idx_deck_enrollment_source ON deck_enrollment(source);
CREATE INDEX idx_deck_enrollment_last_accessed ON deck_enrollment(last_accessed_at);
