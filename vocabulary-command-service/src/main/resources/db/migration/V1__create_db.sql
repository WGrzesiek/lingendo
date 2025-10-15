create table outbox
(
    event_id varchar(36) not null primary key,
    aggregate_id varchar(36) not null,
    created_at timestamp(6) with time zone not null,
    deck_id varchar(36),
    payload jsonb not null,
    retry_count integer not null,
    updated_at timestamp(6) with time zone not null,
    aggregate_type varchar(50) not null
        constraint outbox_aggregate_type_check
            check ( aggregate_type IN ('VOCABULARY', 'SENTENCE') ),
    event_status varchar(50) not null
        constraint outbox_event_status_check
            check (event_status IN ('CREATED', 'PUBLISHED', 'QUEUED', 'RECEIVED', 'VALIDATED', 'PROCESSING', 'COMPLETED', 'RETRYING', 'FAILED', 'DEAD_LETTERED', 'SKIPPED', 'COMPENSATED', 'CANCELED','TIMEOUT')),
    event_type varchar(30) not null
        constraint outbox_event_type_check
            check (event_type IN ('CREATE_SENTENCE', 'CREATE_SENTENCE_FROM_VOCABULARY', 'CREATE_VOCABULARY'))
);
create index idx_outbox_status
    on outbox (event_status);

create index idx_outbox_created
    on outbox (created_at);