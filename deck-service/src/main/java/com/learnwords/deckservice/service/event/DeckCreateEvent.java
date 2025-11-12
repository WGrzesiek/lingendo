package com.learnwords.deckservice.service.event;

import lombok.*;

import java.io.Serializable;
import java.time.Instant;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class DeckCreateEvent implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String deckId;
    private String userId;
    private Long wordCount;
    private Instant createdAt;
    private boolean isPublic;
    private String language;
    private String targetLanguage;
}
