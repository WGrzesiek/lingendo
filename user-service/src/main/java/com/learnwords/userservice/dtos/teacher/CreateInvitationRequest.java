package com.learnwords.userservice.dtos.teacher;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.Instant;

/**
 * DTO żądania utworzenia nowego zaproszenia
 *
 * @param name      opcjonalna nazwa zaproszenia
 * @param maxUses   maksymalna liczba użyć (null = bez limitu)
 * @param expiresAt data wygaśnięcia (null = nie wygasa)
 */
public record CreateInvitationRequest(
        @Size(max = 100, message = "Nazwa zaproszenia może mieć maksymalnie 100 znaków")
        String name,

        @Min(value = 1, message = "Minimalna liczba użyć to 1")
        @Max(value = 1000, message = "Maksymalna liczba użyć to 1000")
        Integer maxUses,

        Instant expiresAt
) {
}
