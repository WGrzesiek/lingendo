package com.learnwords.userservice.dtos.group;

import com.learnwords.userservice.enums.GroupStatus;

import java.time.Instant;

/**
 * DTO odpowiedzi z informacją o grupie
 *
 * @param id          identyfikator grupy
 * @param name        nazwa grupy
 * @param description opis grupy
 * @param color       kolor grupy (hex)
 * @param teacherId   identyfikator nauczyciela (właściciela)
 * @param teacherName imię i nazwisko nauczyciela
 * @param status      status grupy
 * @param memberCount liczba członków grupy
 * @param createdAt   data utworzenia
 * @param updatedAt   data ostatniej modyfikacji
 */
public record GroupResponse(
        String id,
        String name,
        String description,
        String color,
        String teacherId,
        String teacherName,
        GroupStatus status,
        int memberCount,
        Instant createdAt,
        Instant updatedAt
) {
}
