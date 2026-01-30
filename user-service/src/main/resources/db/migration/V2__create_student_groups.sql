CREATE TABLE student_group (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(500),
    teacher_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CONSTRAINT student_group_status_check
            CHECK (status IN ('ACTIVE', 'ARCHIVED', 'DELETED')),
    color VARCHAR(7),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_student_group_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_student_group_teacher_id ON student_group(teacher_id);
CREATE INDEX idx_student_group_status ON student_group(status);

CREATE TABLE group_member (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    group_id VARCHAR(255) NOT NULL,
    student_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CONSTRAINT group_member_status_check
            CHECK (status IN ('ACTIVE', 'REMOVED', 'BLOCKED')),
    joined_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    removed_at TIMESTAMP(6) WITH TIME ZONE,
    added_by_id VARCHAR(255),
    CONSTRAINT uk_group_student
        UNIQUE (group_id, student_id),
    CONSTRAINT fk_group_member_group
        FOREIGN KEY (group_id)
        REFERENCES student_group(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_group_member_student
        FOREIGN KEY (student_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_group_member_added_by
        FOREIGN KEY (added_by_id)
        REFERENCES user_management(id)
        ON DELETE SET NULL
);

CREATE INDEX idx_group_member_group_id ON group_member(group_id);
CREATE INDEX idx_group_member_student_id ON group_member(student_id);
CREATE INDEX idx_group_member_status ON group_member(status);
