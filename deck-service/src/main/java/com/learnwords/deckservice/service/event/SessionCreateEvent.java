package com.learnwords.deckservice.service.event;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

import com.learnwords.deckservice.enums.SessionType;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class SessionCreateEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String sessionId;
    private String deckId;
    private String userId;
    private int totalFlashcards;
    private Instant startedAt;
    private SessionType sessionType;
}
