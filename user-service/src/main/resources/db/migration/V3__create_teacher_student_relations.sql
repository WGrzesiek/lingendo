CREATE TABLE teacher_invitation (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    invitation_code VARCHAR(32) NOT NULL UNIQUE,
    teacher_id VARCHAR(255) NOT NULL,
    name VARCHAR(100),
    max_uses INTEGER,
    current_uses INTEGER NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
        CONSTRAINT teacher_invitation_status_check
            CHECK (status IN ('ACTIVE', 'DEACTIVATED', 'EXHAUSTED', 'EXPIRED')),
    expires_at TIMESTAMP(6) WITH TIME ZONE,
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_teacher_invitation_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE
);

CREATE UNIQUE INDEX idx_invitation_code ON teacher_invitation(invitation_code);
CREATE INDEX idx_invitation_teacher ON teacher_invitation(teacher_id);
CREATE INDEX idx_invitation_status ON teacher_invitation(status);

CREATE TABLE teacher_student (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    teacher_id VARCHAR(255) NOT NULL,
    student_id VARCHAR(255) NOT NULL,
    status VARCHAR(20) NOT NULL
        CONSTRAINT teacher_student_status_check
            CHECK (status IN ('INVITED', 'ACTIVE', 'REJECTED', 'BLOCKED')),
    created_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    accepted_at TIMESTAMP(6) WITH TIME ZONE,
    updated_at TIMESTAMP(6) WITH TIME ZONE NOT NULL,
    CONSTRAINT uk_teacher_student
        UNIQUE (teacher_id, student_id),
    CONSTRAINT fk_teacher_student_teacher
        FOREIGN KEY (teacher_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_teacher_student_student
        FOREIGN KEY (student_id)
        REFERENCES user_management(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_teacher_student_teacher_id ON teacher_student(teacher_id);
CREATE INDEX idx_teacher_student_student_id ON teacher_student(student_id);
CREATE INDEX idx_teacher_student_status ON teacher_student(status);
