package com.learnwords.deckservice.dto.course;

import com.learnwords.deckservice.enums.SessionStatus;

public record SessionInfo(
    String sessionId,
    Integer sessionNumber,
    SessionStatus status
) {
}
