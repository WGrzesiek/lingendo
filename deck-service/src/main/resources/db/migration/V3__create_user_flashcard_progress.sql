CREATE TABLE user_flashcard_progress (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    user_id VARCHAR(36) NOT NULL,
    flashcard_id VARCHAR(36) NOT NULL,
    enrollment_id VARCHAR(36) NOT NULL,
    is_learned BOOLEAN NOT NULL DEFAULT FALSE,
    is_skipped BOOLEAN NOT NULL DEFAULT FALSE,
    repetition_count INTEGER NOT NULL DEFAULT 0,
    algorithm_state JSONB NOT NULL DEFAULT '{}',
    next_review_at TIMESTAMP(6) WITH TIME ZONE,
    learning_phase VARCHAR(20) NOT NULL
        CONSTRAINT user_flashcard_progress_phase_check
            CHECK (learning_phase IN ('NEW', 'LEARNING', 'REVIEW', 'GRADUATED', 'RELEARNING')),
    last_shown_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_user_flashcard_progress_flashcard
        FOREIGN KEY (flashcard_id)
        REFERENCES flashcard(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_flashcard_progress_enrollment
        FOREIGN KEY (enrollment_id)
        REFERENCES deck_enrollment(id)
        ON DELETE CASCADE,
    CONSTRAINT uk_user_flashcard_progress
        UNIQUE (user_id, flashcard_id, enrollment_id)
);

CREATE INDEX idx_user_flashcard_progress_user_id ON user_flashcard_progress(user_id);
CREATE INDEX idx_user_flashcard_progress_flashcard_id ON user_flashcard_progress(flashcard_id);
CREATE INDEX idx_user_flashcard_progress_enrollment_id ON user_flashcard_progress(enrollment_id);
CREATE INDEX idx_user_flashcard_progress_phase ON user_flashcard_progress(learning_phase);
CREATE INDEX idx_user_flashcard_progress_next_review ON user_flashcard_progress(next_review_at);
CREATE INDEX idx_user_flashcard_progress_is_learned ON user_flashcard_progress(is_learned);
