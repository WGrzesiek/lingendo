CREATE TABLE user_friendship (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id_1 VARCHAR(255) NOT NULL,
    user_id_2 VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
        CONSTRAINT user_friendship_status_check
            CHECK (status IN ('PENDING', 'ACCEPTED', 'BLOCKED')),
    requested_by_user_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_user_friendship
        UNIQUE (user_id_1, user_id_2),
    CONSTRAINT fk_user_friendship_user1
        FOREIGN KEY (user_id_1)
        REFERENCES user_management(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_friendship_user2
        FOREIGN KEY (user_id_2)
        REFERENCES user_management(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_user_friendship_requested_by
        FOREIGN KEY (requested_by_user_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_user_friendship_user1 ON user_friendship(user_id_1);
CREATE INDEX idx_user_friendship_user2 ON user_friendship(user_id_2);
CREATE INDEX idx_user_friendship_status ON user_friendship(status);

CREATE TABLE user_password_reset_token (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    token VARCHAR(100) NOT NULL UNIQUE,
    expires_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    used BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_password_reset_token_user
        FOREIGN KEY (user_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_password_reset_token_user_id ON user_password_reset_token(user_id);
CREATE INDEX idx_password_reset_token_token ON user_password_reset_token(token);
CREATE INDEX idx_password_reset_token_expires_at ON user_password_reset_token(expires_at);
