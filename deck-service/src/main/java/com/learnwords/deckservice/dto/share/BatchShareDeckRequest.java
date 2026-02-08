package com.learnwords.deckservice.dto.share;

import com.learnwords.deckservice.enums.ShareTargetType;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

/**
 * DTO żądania batch udostępnienia talii wielu grupom/użytkownikom.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchShareDeckRequest {

    @NotNull(message = "ID talii jest wymagane")
    private String deckId;

    @NotNull(message = "Typ celu udostępnienia jest wymagany")
    private ShareTargetType targetType;

    @NotEmpty(message = "Lista celów nie może być pusta")
    private List<String> targetIds;

    private String message;

    private Instant expiresAt;
}
