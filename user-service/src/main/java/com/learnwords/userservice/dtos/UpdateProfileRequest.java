package com.learnwords.userservice.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

/**
 * DTO do aktualizacji profilu użytkownika.
 */
public record UpdateProfileRequest(
        @Size(max = 50, message = "Imię może mieć maksymalnie 50 znaków")
        String firstName,

        @Size(max = 50, message = "Nazwisko może mieć maksymalnie 50 znaków")
        String lastName,

        @Email(message = "Podaj poprawny adres email")
        String email
) {}
