-- V1__init.sql
-- Inicjalizacja tabel dla koog-service

CREATE TABLE IF NOT EXISTS sentence_generation_job (
    job_id VARCHAR(36) NOT NULL PRIMARY KEY,
    correlation_id       VARCHAR(255)                NOT NULL,
    items_total          INTEGER                     NOT NULL,
    items_succeeded      INTEGER,
    items_failed         INTEGER,
    requested_by_user_id VARCHAR(255)                NOT NULL,
    status               VARCHAR(255)                NOT NULL,
    schema_version       INTEGER                     NOT NULL,
    requested_at         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    started_at           TIMESTAMP WITHOUT TIME ZONE,
    finished_at          TIMESTAMP WITHOUT TIME ZONE
);

CREATE INDEX IF NOT EXISTS idx_job_correlation_id ON sentence_generation_job(correlation_id);
CREATE INDEX IF NOT EXISTS idx_job_user_id ON sentence_generation_job(requested_by_user_id);
CREATE INDEX IF NOT EXISTS idx_job_status ON sentence_generation_job(status);

CREATE TABLE IF NOT EXISTS sentence_generation_item
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
    input_tokens_count  integer,
    output_tokens_count integer,
    total_tokens_count  integer,
    created_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_sentence_generation_item PRIMARY KEY (item_id)
);

ALTER TABLE sentence_generation_item
    ADD CONSTRAINT FK_SENTENCE_GENERATION_ITEM_ON_JOB FOREIGN KEY (job_id) REFERENCES sentence_generation_job (job_id);

CREATE INDEX IF NOT EXISTS idx_item_job_id ON sentence_generation_item(job_id);
CREATE INDEX IF NOT EXISTS idx_item_word_id ON sentence_generation_item(word_id);
CREATE INDEX IF NOT EXISTS idx_item_status ON sentence_generation_item(status);

-- Nazwa koog_outbox (nie `outbox`) — unika kolizji z głównym public.outbox we wspólnej bazie lingendo.
CREATE TABLE IF NOT EXISTS koog_outbox
(
    event_id      VARCHAR(255)                NOT NULL,
    agregate_type VARCHAR(255)                NOT NULL,
    agregate_id   VARCHAR(255)                NOT NULL,
    event_type    VARCHAR(255)                NOT NULL,
    payload       JSONB,
    status        VARCHAR(255)                NOT NULL,
    retry_count   INTEGER                     NOT NULL,
    created_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at    TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_koog_outbox PRIMARY KEY (event_id)
);

CREATE INDEX IF NOT EXISTS idx_koog_outbox_status ON koog_outbox(status);
CREATE INDEX IF NOT EXISTS idx_koog_outbox_created_at ON koog_outbox(created_at);