package com.learnwords.vocabularycommandservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO do tworzenia nowego zdania przykładowego (Command Side).
 * 
 * <p>Używane w endpointach Command Service do przyjmowania danych od klienta
 * podczas tworzenia nowego zdania przykładowego. Może być używane samodzielnie
 * lub jako część {@link CreateWordDto} przy tworzeniu słówka z przykładami.
 * 
 * <p>Zawiera walidacje Jakarta Bean Validation, które są automatycznie
 * sprawdzane przez Spring przed wywołaniem metody kontrolera.
 * 
 * <p>Walidacje:
 * <ul>
 *   <li>sentence - nie może być puste ({@code @NotBlank})</li>
 *   <li>translation - nie może być puste ({@code @NotBlank})</li>
 * </ul>
 * 
 * <p>Przykład użycia:
 * <pre>
 * CreateSentenceDto dto = new CreateSentenceDto();
 * dto.setSentence("Hello, how are you?");
 * dto.setTranslation("Cześć, jak się masz?");
 * </pre>
 * 
 * <p>Przykład JSON:
 * <pre>
 * {
 *   "sentence": "Hello, how are you?",
 *   "translation": "Cześć, jak się masz?"
 * }
 * </pre>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see SendSentenceDto
 * @see CreateWordDto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateSentenceDto {

    /**
     * Zdanie przykładowe w języku źródłowym.
     * 
     * <p>Nie może być puste. To pole jest wymagane.
     * Walidowane przez {@code @NotBlank}.
     * 
     * <p>Przykład: {@code "Hello, how are you?"}
     */
    @NotBlank(message = "Wypelnij")
    private String sentence;

    /**
     * Tłumaczenie zdania.
     * 
     * <p>Nie może być puste. To pole jest wymagane.
     * Walidowane przez {@code @NotBlank}.
     * 
     * <p>Przykład: {@code "Cześć, jak się masz?"}
     */
    @NotBlank(message = "Wypelnij")
    private String translation;
}
