package com.learnwords.userservice.dtos.group;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO żądania utworzenia nowej grupy
 *
 * @param name        nazwa grupy (wymagana)
 * @param description opcjonalny opis grupy
 * @param color       opcjonalny kolor grupy (format hex, np. #FF5733)
 */
public record CreateGroupRequest(
        @NotBlank(message = "Nazwa grupy jest wymagana")
        @Size(min = 2, max = 100, message = "Nazwa grupy musi mieć od 2 do 100 znaków")
        String name,

        @Size(max = 500, message = "Opis grupy może mieć maksymalnie 500 znaków")
        String description,

        @Size(max = 7, message = "Kolor musi być w formacie hex (np. #FF5733)")
        String color
) {
}
