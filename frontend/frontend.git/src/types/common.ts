/**
 * Wspólne typy używane w całej aplikacji
 */

/**
 * Standardowa odpowiedź błędu z API
 */
export interface ApiErrorResponse {
  status: number;
  message: string;
}

/**
 * Dostępne języki w systemie
 */
export type Language =
  | "POLISH"
  | "ENGLISH"
  | "SPANISH"
  | "GERMAN"
  | "FRENCH"
  | "ITALIAN";

/**
 * Typy właściciela talii
 */
export type DeckOwnerType = "I" | "TEACHER" | "FRIEND" | "COMMUNITY";

/**
 * Algorytmy nauki dostępne w systemie
 */
export type LearnAlgorithm =
  | "GRZESIEK_ALGORITHM"
  | "LEINER_ALGORITHM"
  | "TEST_ALGORITHM";

/**
 * Status sesji nauki
 */
export type SessionStatus = "ACTIVE" | "COMPLETED" | "PAUSED" | "ABANDONED";

/**
 * Typ sesji nauki
 */
export type SessionType = "LEARNING" | "REVIEW" | "TEST" | "PRACTICE";
