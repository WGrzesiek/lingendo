package com.learnwords.userservice.dtos.teacher;

import com.learnwords.userservice.enums.InvitationStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o zaproszeniu
 *
 * @param id             identyfikator zaproszenia
 * @param invitationCode unikalny kod zaproszenia
 * @param invitationUrl  pełny URL zaproszenia
 * @param name           nazwa zaproszenia
 * @param maxUses        maksymalna liczba użyć
 * @param currentUses    aktualna liczba użyć
 * @param status         status zaproszenia
 * @param expiresAt      data wygaśnięcia
 * @param createdAt      data utworzenia
 */
public record InvitationResponse(
        String id,
        String invitationCode,
        String invitationUrl,
        String name,
        Integer maxUses,
        int currentUses,
        InvitationStatus status,
        Instant expiresAt,
        Instant createdAt
) {
}
