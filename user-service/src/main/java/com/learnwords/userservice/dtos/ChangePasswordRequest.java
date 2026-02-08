package com.learnwords.userservice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO do zmiany hasła użytkownika.
 *
 * @param currentPassword aktualne hasło
 * @param newPassword     nowe hasło (min. 8 znaków)
 */
public record ChangePasswordRequest(
        @NotBlank(message = "Aktualne hasło jest wymagane")
        String currentPassword,

        @NotBlank(message = "Nowe hasło jest wymagane")
        @Size(min = 8, message = "Nowe hasło musi mieć minimum 8 znaków")
        String newPassword
) {}
