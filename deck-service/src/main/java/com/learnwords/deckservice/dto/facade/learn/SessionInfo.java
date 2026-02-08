package com.learnwords.deckservice.dto.facade.learn;

import com.learnwords.deckservice.enums.SessionStatus;

public record SessionInfo(
    String sessionId,
    Integer sessionNumber,
    SessionStatus status
) {
}
