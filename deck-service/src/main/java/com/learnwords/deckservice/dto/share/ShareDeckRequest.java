package com.learnwords.deckservice.dto.share;

import com.learnwords.deckservice.enums.ShareTargetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * DTO żądania udostępnienia talii.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareDeckRequest {

    @NotBlank(message = "ID talii jest wymagane")
    private String deckId;

    @NotNull(message = "Typ celu udostępnienia jest wymagany")
    private ShareTargetType targetType;

    private String targetId;

    private String message;

    private Instant expiresAt;
}
