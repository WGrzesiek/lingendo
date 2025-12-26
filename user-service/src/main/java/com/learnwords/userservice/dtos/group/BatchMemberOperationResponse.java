package com.learnwords.userservice.dtos.group;

import java.util.List;

/**
 * DTO odpowiedzi z wynikiem operacji batch na członkach grupy
 *
 * @param success  lista ID uczniów dla których operacja się powiodła
 * @param failed   lista ID uczniów dla których operacja się nie powiodła
 * @param errors   lista błędów (dla nieudanych operacji)
 */
public record BatchMemberOperationResponse(
        List<String> success,
        List<String> failed,
        List<String> errors
) {
}
