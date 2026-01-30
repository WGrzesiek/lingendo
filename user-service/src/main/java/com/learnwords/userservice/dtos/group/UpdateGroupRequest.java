package com.learnwords.userservice.dtos.group;

import jakarta.validation.constraints.Size;

/**
 * DTO żądania aktualizacji grupy
 *
 * @param name        nowa nazwa grupy
 * @param description nowy opis grupy
 * @param color       nowy kolor grupy
 */
public record UpdateGroupRequest(
        @Size(min = 2, max = 100, message = "Nazwa grupy musi mieć od 2 do 100 znaków")
        String name,

        @Size(max = 500, message = "Opis grupy może mieć maksymalnie 500 znaków")
        String description,

        @Size(max = 7, message = "Kolor musi być w formacie hex (np. #FF5733)")
        String color
) {
}
