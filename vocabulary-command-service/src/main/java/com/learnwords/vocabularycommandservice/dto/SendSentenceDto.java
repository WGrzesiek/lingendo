package com.learnwords.vocabularycommandservice.dto;

/**
 * DTO odpowiedzi po utworzeniu zdania przykładowego (Command Side).
 * 
 * <p>Zawiera wszystkie dane utworzonego zdania, w tym wygenerowane ID,
 * zdanie w języku źródłowym, tłumaczenie oraz ID słówka, do którego
 * zdanie należy. Używane jako odpowiedź z endpointów tworzących zdania
 * oraz jako payload eventów w Outbox Pattern.
 * 
 * <p>Record zapewnia immutability - wszystkie pola są final i nie można ich zmienić
 * po utworzeniu obiektu.
 * 
 * <p>Przykład użycia:
 * <pre>
 * SendSentenceDto sentence = new SendSentenceDto(
 *     "550e8400-e29b-41d4-a716-446655440000",
 *     "Hello, how are you?",
 *     "Cześć, jak się masz?",
 *     "word-123"
 * );
 * </pre>
 * 
 * @param id unikalny identyfikator zdania (UUID)
 * @param sentence zdanie w języku źródłowym
 * @param translation tłumaczenie zdania
 * @param wordId ID słówka, do którego należy to zdanie
 * 
 * @author Grzegorz Wawrzeń
 * @version 1.0
 * @since 2025-11-11
 * @see CreateSentenceDto
 */
public record SendSentenceDto(String id, String sentence, String translation, String wordId) {
}
