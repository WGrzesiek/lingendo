CREATE TABLE deck_share (
    id VARCHAR(36) PRIMARY KEY NOT NULL,
    deck_id VARCHAR(36) NOT NULL
        CONSTRAINT fk_deck_share_deck REFERENCES deck(id) ON DELETE CASCADE,
    owner_id VARCHAR(36) NOT NULL,
    target_type VARCHAR(20) NOT NULL,
    target_id VARCHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    message TEXT,
    shared_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT NOW(),
    expires_at TIMESTAMP(6) WITH TIME ZONE,
    revoked_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL DEFAULT NOW(),

    CONSTRAINT deck_share_target_type_check
        CHECK (target_type IN ('GROUP', 'ALL_STUDENTS', 'ALL_FRIENDS', 'USER')),

    CONSTRAINT deck_share_status_check
        CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED')),

    CONSTRAINT uq_deck_share_deck_target UNIQUE (deck_id, target_type, target_id)
);

CREATE INDEX idx_deck_share_deck_id ON deck_share(deck_id);
CREATE INDEX idx_deck_share_owner_id ON deck_share(owner_id);
CREATE INDEX idx_deck_share_target_type ON deck_share(target_type);
CREATE INDEX idx_deck_share_target_id ON deck_share(target_id);
CREATE INDEX idx_deck_share_status ON deck_share(status);

CREATE INDEX idx_deck_share_status_target ON deck_share(status, target_type, target_id);

CREATE INDEX idx_deck_share_active_for_user ON deck_share(target_type, target_id, status)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_deck_share_active_global ON deck_share(target_type, owner_id, status)
    WHERE status = 'ACTIVE' AND target_type IN ('ALL_STUDENTS', 'ALL_FRIENDS');
