/**
 * Standardowa odpowiedź paginowana z API
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * Standardowa odpowiedź błędu z API
 */
export interface ApiErrorResponse {
  status: number;
  message: string;
}

// =====================
// LANGUAGE TYPES
// =====================

/**
 * Dostępne języki w systemie
 */
export const languageValues = [
  'POLISH',
  'ENGLISH',
  'SPANISH',
  'GERMAN',
  'FRENCH',
  'ITALIAN',
] as const;

export type Language = (typeof languageValues)[number];

/**
 * Konfiguracja języków (labele po polsku)
 */
export const languageConfig: Record<Language, { label: string }> = {
  POLISH: { label: 'Polski' },
  ENGLISH: { label: 'Angielski' },
  SPANISH: { label: 'Hiszpański' },
  GERMAN: { label: 'Niemiecki' },
  FRENCH: { label: 'Francuski' },
  ITALIAN: { label: 'Włoski' },
};

/**
 * Lista języków do selectów
 */
export const LANGUAGES = languageValues.map((value) => ({
  value,
  label: languageConfig[value].label,
}));
