CREATE TABLE deck_share (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    deck_id VARCHAR(36) NOT NULL,
    owner_id VARCHAR(36) NOT NULL,
    target_type VARCHAR(20) NOT NULL
        CONSTRAINT deck_share_target_type_check
            CHECK (target_type IN ('GROUP', 'ALL_STUDENTS', 'ALL_FRIENDS', 'USER')),
    target_id VARCHAR(36),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CONSTRAINT deck_share_status_check
            CHECK (status IN ('ACTIVE', 'REVOKED', 'EXPIRED')),
    message VARCHAR(255),
    shared_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP(6) WITH TIME ZONE,
    revoked_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_deck_share_deck
        FOREIGN KEY (deck_id)
        REFERENCES deck(id)
        ON DELETE CASCADE,
    CONSTRAINT uk_deck_share_target
        UNIQUE (deck_id, target_type, target_id)
);

CREATE INDEX idx_deck_share_deck_id ON deck_share(deck_id);
CREATE INDEX idx_deck_share_owner_id ON deck_share(owner_id);
CREATE INDEX idx_deck_share_target_type ON deck_share(target_type);
CREATE INDEX idx_deck_share_target_id ON deck_share(target_id);
CREATE INDEX idx_deck_share_status ON deck_share(status);
CREATE INDEX idx_deck_share_target ON deck_share(target_type, target_id);

CREATE INDEX idx_deck_share_active ON deck_share(target_type, target_id, status)
    WHERE status = 'ACTIVE';

CREATE INDEX idx_deck_share_global ON deck_share(target_type, owner_id, status)
    WHERE status = 'ACTIVE' AND target_type IN ('ALL_STUDENTS', 'ALL_FRIENDS');
