package com.learnwords.userservice.dtos;

import com.learnwords.userservice.enums.AccountType;
import com.learnwords.userservice.enums.UserType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @Valid

    @NotNull(message = "Imie jest wymagane")
    @NotBlank(message = "Wprowadź swoje imię")
    private String firstName;

    @NotNull(message = "Nazwisko jest wymagane")
    @NotBlank(message = "Wprowadź swoje nazwisko")
    private String lastName;

    @NotNull(message = "Typ użytkownika jest wymagany")
    @Enumerated(value = EnumType.STRING)
    private UserType userType;

    @NotNull(message = "Typ konta jest wymagany")
    @Enumerated(value = EnumType.STRING)
    private AccountType accountType;

    @NotNull(message = "Nazwa użytkownika jest wymagana")
    @NotBlank(message = "Wprowadź nazwę użytkownika")
    @Size(min = 5, max = 20, message = "Nazwa użytkownika musi mieć od 5 do 20 znaków")
    private String username;

    @NotNull(message = "Email jest wymagany")
    @NotBlank(message = "Wprowadź email")
    @Email(message = "Email jest niepoprawny")
    private String email;

    @NotNull(message = "Hasło jest wymagane")
    @NotBlank(message = "Wprowadź hasło")
    @Size(min = 8, max = 20, message = "Hasło musi mieć od 8 do 20 znaków")
    private String password;
}
