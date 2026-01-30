package com.learnwords.userservice.dtos.group;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO żądania usunięcia uczniów z grupy
 *
 * @param studentIds lista identyfikatorów uczniów do usunięcia
 */
public record RemoveMembersRequest(
        @NotEmpty(message = "Lista uczniów nie może być pusta")
        List<String> studentIds
) {
}
