CREATE TABLE deck (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    owner_id VARCHAR(36) NOT NULL,
    visibility VARCHAR(20) NOT NULL DEFAULT 'PRIVATE'
        CONSTRAINT deck_visibility_check
            CHECK (visibility IN ('PRIVATE', 'PUBLIC')),
    word_count INTEGER NOT NULL DEFAULT 0,
    how_many_flashcards_for_one_session BIGINT NOT NULL DEFAULT 20,
    language_from VARCHAR(20) NOT NULL
        CONSTRAINT deck_language_from_check
            CHECK (language_from IN ('POLISH', 'ENGLISH', 'GERMAN', 'SPANISH', 'FRENCH', 'ITALIAN')),
    language_to VARCHAR(20) NOT NULL
        CONSTRAINT deck_language_to_check
            CHECK (language_to IN ('POLISH', 'ENGLISH', 'GERMAN', 'SPANISH', 'FRENCH', 'ITALIAN')),
    difficulty VARCHAR(20)
        CONSTRAINT deck_difficulty_check
            CHECK (difficulty IN ('EASY', 'MEDIUM', 'HARD')),
    category VARCHAR(100)
        CONSTRAINT deck_category_check
            CHECK (category IN ('GENERAL', 'BUSINESS', 'TRAVEL', 'ACADEMIC', 'MEDICAL', 'LEGAL', 'TECHNOLOGY', 'DAILY_LIFE', 'SLANG', 'IDIOMS')),
    learn_algorithm VARCHAR(30) NOT NULL
        CONSTRAINT deck_learn_algorithm_check
            CHECK (learn_algorithm IN ('GRZESIEK_ALGORITHM', 'LEINER_ALGORITHM', 'TEST_ALGORITHM')),
    owner VARCHAR(20) NOT NULL DEFAULT 'I'
        CONSTRAINT deck_owner_type_check
            CHECK (owner IN ('I', 'TEACHER', 'FRIEND', 'COMMUNITY')),
    review_schedule VARCHAR(20) NOT NULL DEFAULT 'AUTO'
        CONSTRAINT deck_review_schedule_check
            CHECK (review_schedule IN ('AUTO', 'DAILY', 'EVERY_OTHER_DAY', 'WEEKLY', 'CUSTOM')),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL
);

CREATE INDEX idx_deck_owner_id ON deck(owner_id);
CREATE INDEX idx_deck_visibility ON deck(visibility);
CREATE INDEX idx_deck_category ON deck(category);
CREATE INDEX idx_deck_language_from ON deck(language_from);
CREATE INDEX idx_deck_language_to ON deck(language_to);
