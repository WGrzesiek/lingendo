CREATE TABLE user_management
(
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    account_type VARCHAR(20) NOT NULL
        CONSTRAINT user_management_account_type_check
            CHECK (account_type IN ('BASIC', 'PREMIUM', 'STUDENT', 'TEACHER')),
    confirmed BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    email VARCHAR(100) NOT NULL
        CONSTRAINT email_unique UNIQUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    first_name VARCHAR(255),
    last_login TIMESTAMP(6) WITH TIME ZONE,
    last_name VARCHAR(255),
    last_password_change TIMESTAMP(6) WITH TIME ZONE,
    login_count INTEGER NOT NULL DEFAULT 0,
    streak INTEGER NOT NULL DEFAULT 0,
    password VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    user_type VARCHAR(20) NOT NULL
        CONSTRAINT user_management_user_type_check
            CHECK (user_type IN ('NORMAL', 'ADMIN')),
    username VARCHAR(50) NOT NULL
        CONSTRAINT username_unique UNIQUE
);

CREATE INDEX idx_user_management_email ON user_management(email);
CREATE INDEX idx_user_management_username ON user_management(username);
CREATE INDEX idx_user_management_account_type ON user_management(account_type);
CREATE INDEX idx_user_management_user_type ON user_management(user_type);
CREATE INDEX idx_user_management_enabled ON user_management(enabled);