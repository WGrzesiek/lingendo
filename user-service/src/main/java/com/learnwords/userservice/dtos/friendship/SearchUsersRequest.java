package com.learnwords.userservice.dtos.friendship;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO żądania wyszukania użytkowników do dodania do znajomych
 *
 * @param query fraza wyszukiwania (username lub email)
 */
public record SearchUsersRequest(
        @NotBlank(message = "Fraza wyszukiwania jest wymagana")
        @Size(min = 2, max = 100, message = "Fraza musi mieć od 2 do 100 znaków")
        String query
) {
}
