package com.learnwords.userservice.dtos.group;

import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * DTO żądania dodania uczniów do grupy
 *
 * @param studentIds lista identyfikatorów uczniów do dodania
 */
public record AddMembersRequest(
        @NotEmpty(message = "Lista uczniów nie może być pusta")
        List<String> studentIds
) {
}
