/**
 * Typy związane ze słówkami (words) i zdaniami (sentences)
 */

/**
 * DTO zdania
 */
export interface SentenceDto {
  id: string;
  sentence: string;
  translation: string;
}

/**
 * DTO słówka
 */
export interface WordDto {
  id: string;
  word: string;
  translations: string[];
  sentences: SentenceDto[];
  sentencesAI: SentenceDto[];
}
