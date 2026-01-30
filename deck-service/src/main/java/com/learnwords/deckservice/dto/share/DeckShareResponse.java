package com.learnwords.deckservice.dto.share;

import com.learnwords.deckservice.enums.ShareStatus;
import com.learnwords.deckservice.enums.ShareTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o udostępnieniu talii.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeckShareResponse {

    private String id;
    private String deckId;
    private String deckName;
    private String ownerId;
    private String ownerName;
    private ShareTargetType targetType;
    private String targetId;
    private String targetName;
    private ShareStatus status;
    private String message;
    private Instant sharedAt;
    private Instant expiresAt;
    private Instant revokedAt;
}
