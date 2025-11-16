package com.learnwords.vocabularycommandservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO do tworzenia nowego słówka w systemie (Command Side).
 * 
 * <p>Używane w endpointach Command Service do przyjmowania danych od klienta
 * podczas tworzenia nowego słówka. Zawiera walidacje Jakarta Bean Validation,
 * które są automatycznie sprawdzane przez Spring przed wywołaniem metody kontrolera.
 * 
 * <p>Walidacje:
 * <ul>
 *   <li>word - nie może być null ani puste ({@code @NotNull, @NotBlank})</li>
 *   <li>translations - musi zawierać przynajmniej jedno tłumaczenie ({@code @NotNull, @Size(min=1)})</li>
 *   <li>sentences - opcjonalne, może być null lub pusta lista ({@code @Valid} dla zagnieżdżonych DTO)</li>
 * </ul>
 * 
 * <p>Przykład użycia:
 * <pre>
 * CreateWordDto dto = new CreateWordDto();
 * dto.setWord("hello");
 * dto.setTranslations(List.of("cześć", "witaj"));
 * dto.setSentences(List.of(
 *     new CreateSentenceDto("Hello world", "Witaj świecie")
 * ));
 * </pre>
 * 
 * <p>Przykład JSON:
 * <pre>
 * {
 *   "word": "hello",
 *   "translations": ["cześć", "witaj"],
 *   "sentences": [
 *     {
 *       "sentence": "Hello world",
 *       "translation": "Witaj świecie"
 *     }
 *   ]
 * }
 * </pre>
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see CreateSentenceDto
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWordDto {
    
    /**
     * Słowo w języku źródłowym.
     * 
     * <p>Nie może być null ani puste. To pole jest wymagane.
     * Walidowane przez {@code @NotNull} i {@code @NotBlank}.
     */
    @NotBlank(message = "To pole jest wymagane")
    private String word;

    /**
     * Lista tłumaczeń słowa.
     * 
     * <p>Musi zawierać przynajmniej jedno tłumaczenie. To pole jest wymagane.
     * Walidowane przez {@code @NotEmpty} i {@code @Size(min=1)}.
     * 
     * <p>Przykład: {@code ["cześć", "witaj", "hej"]}
     */
    @NotEmpty(message = "Podaj przynajmniej jedno tłumaczenie")
    @Size(min = 1, message = "Podaj przynajmniej jedno tłumaczenie")
    private List<String> translations;

    /**
     * Opcjonalne przykładowe zdania używające tego słowa.
     * 
     * <p>Może być null lub pusta lista. Każde zdanie zostanie utworzone
     * jako osobna encja w bazie danych i powiązane z tym słówkiem.
     * 
     * <p>Walidowane przez {@code @Valid} - każde zagnieżdżone DTO będzie
     * również zwalidowane zgodnie ze swoimi ograniczeniami.
     * 
     * @see CreateSentenceDto
     */
    @Valid
    private List<CreateSentenceDto> sentences;
}
