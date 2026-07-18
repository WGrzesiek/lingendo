-- Główna tabela outbox (transactional outbox). Źródło głównego Debezium Server → topiki <aggregate>.events.
-- Jawnie public.outbox (współdzielona, niezależna od schematu serwisu; Debezium czyta public.outbox).
-- Kolumny mapowane z encji Outbox.java (Hibernate snake_case) + pola wymagane przez EventRouter:
-- event_id / aggregate_id / aggregate_type / event_type / payload.
CREATE TABLE IF NOT EXISTS public.outbox
(
    event_id       VARCHAR(36)  NOT NULL,
    aggregate_id   VARCHAR(36)  NOT NULL,
    aggregate_type VARCHAR(50)  NOT NULL,
    event_type     VARCHAR(30)  NOT NULL,
    payload        JSONB        NOT NULL,
    event_status   VARCHAR(50)  NOT NULL,
    retry_count    INTEGER      NOT NULL DEFAULT 0,
    deck_id        VARCHAR(36),
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,
    CONSTRAINT pk_outbox PRIMARY KEY (event_id)
);

CREATE INDEX IF NOT EXISTS idx_outbox_status ON public.outbox (event_status);
CREATE INDEX IF NOT EXISTS idx_outbox_created ON public.outbox (created_at);
