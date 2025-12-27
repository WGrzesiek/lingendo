package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TeacherStudentRemovedEvent(
        Instant eventTime,
        String teacherId,
        String studentId,
        String reason  // REMOVED_BY_TEACHER, LEFT_BY_STUDENT, BLOCKED
) implements DomainEvent {}
