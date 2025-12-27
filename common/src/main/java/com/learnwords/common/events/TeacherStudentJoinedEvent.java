package com.learnwords.common.events;

import lombok.Builder;

import java.time.Instant;

@Builder
public record TeacherStudentJoinedEvent(
        Instant eventTime,
        String teacherId,
        String studentId,
        String studentUsername
) implements DomainEvent {}
