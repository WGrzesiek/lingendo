 CREATE TABLE deck (
    id varchar(36) PRIMARY KEY NOT NULL,
    user_id varchar(36) NOT NULL,
    name varchar(100) NOT NULL,
    description varchar(255),
    word_count int NOT NULL,
    how_many_flashcards_for_one_session bigint NOT NULL,
    is_public boolean NOT NULL,
    language_from varchar(255) NOT NULL,
    language_to varchar(255) NOT NULL,
    learn_algorithm varchar(255) NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    CONSTRAINT deck_language_from_check
        CHECK (language_from IN ('POLISH', 'ENGLISH')),
    CONSTRAINT deck_language_to_check
        CHECK (language_to IN ('POLISH', 'ENGLISH')),
    CONSTRAINT deck_learn_algorithm_check
        CHECK (learn_algorithm IN ('GRZESIEK_ALGORITHM', 'LEINER_ALGORITHM', 'TEST_ALGORITHM'))
);

CREATE TABLE flashcard (
    id varchar(36) PRIMARY KEY NOT NULL,
    algorithm_state jsonb NOT NULL,
    correct_answers integer,
    total_attempts integer,
    is_learned boolean NOT NULL,
    is_skipped boolean NOT NULL,
    created_at timestamp(6) with time zone NOT NULL,
    updated_at timestamp(6) with time zone NOT NULL,
    word_id varchar(36) NOT NULL,
    deck_id varchar(36) NOT NULL
       CONSTRAINT fk_flashcard_deck REFERENCES deck(id)
);
create table session
(
    id varchar(255) not null primary key,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    deck_id varchar(36) not null
        constraint fk_session_deck
            references deck(id)
);
create table session_flashcard
(
    id varchar(255) not null primary key,
    created_at timestamp(6) with time zone not null,
    updated_at timestamp(6) with time zone not null,
    flashcard_id varchar(36) not null
        constraint fk_session_flashcard_flashcard
            references flashcard,
    learning_session_id varchar(255) not null
        constraint fk_session_flashcard_session
            references session(id)
);