CREATE TABLE sentence_generation_item
(
    item_id        VARCHAR(255)                NOT NULL,
    job_id         VARCHAR(255)                NOT NULL,
    word_id        VARCHAR(255)                NOT NULL,
    language_from  VARCHAR(255)                NOT NULL,
    language_to    VARCHAR(255)                NOT NULL,
    level          VARCHAR(255)                NOT NULL,
    category       VARCHAR(255)                NOT NULL,
    prompt_version INTEGER                     NOT NULL,
    model          VARCHAR(255)                NOT NULL,
    status         VARCHAR(255)                NOT NULL,
    attempts       INTEGER                     NOT NULL,
    result_json    JSONB,
    error_code     VARCHAR(255),
    error_message  VARCHAR(255),
    cost_estimate  DOUBLE PRECISION,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_sentence_generation_item PRIMARY KEY (item_id)
);

ALTER TABLE sentence_generation_item
    ADD CONSTRAINT FK_SENTENCE_GENERATION_ITEM_ON_JOB FOREIGN KEY (job_id) REFERENCES sentence_generation_job (job_id);