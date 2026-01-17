// NOTE: SentenceDto jest podobny do WordSentence z @/features/deck/types/flashcard.types.ts
// ale ma inną strukturę (id wymagane), więc tworzymy osobny typ

/**
 * Zdanie przykładowe ze słówkiem
 */
export interface SentenceDto {
  id: string;
  sentence: string;
  translation: string;
}

/**
 * DTO słówka z API
 */
export interface WordDto {
  id: string;
  word: string;
  translations: string[];
  sentences: SentenceDto[];
  sentencesAI: SentenceDto[];
}

/**
 * DTO fiszki z API
 */
export interface FlashcardDto {
  id: string;
  wordDto: WordDto;
}

/**
 * Numer sesji dla fiszki
 */
export interface SessionNumber {
  flashcardId: string;
  sessionNumber: number;
}

export const flashcardPhaseValues = ['NEW', 'LEARNING', 'REVIEW', 'RELEARNING'] as const;
export type FlashcardPhase = (typeof flashcardPhaseValues)[number];

/**
 * Postęp użytkownika dla pojedynczej fiszki
 */
export interface UserFlashcardProgressDto {
  id: string;
  flashcardId: string;
  enrollmentId: string;
  userId: string;
  phase: FlashcardPhase;
  isLearned: boolean;
  isSkipped: boolean;
  repetitionCount: number;
  nextReviewAt: string | null;
  algorithmState: string;
}

/**
 * Element zawartości kursu (fiszka + postęp + numer sesji)
 */
export interface CourseContentItem {
  flashcard: FlashcardDto;
  userFlashcardProgress: UserFlashcardProgressDto;
  sessionNumber: number | null;
}

/**
 * Słówko kursu - zmapowana wersja CourseContentItem
 */
export interface CourseWord {
  id: string;
  flashcardId: string;
  word: string;
  translations: string[];
  sentences: SentenceDto[];
  sentencesAI: SentenceDto[];
  phase: FlashcardPhase;
  isLearned: boolean;
  isSkipped: boolean;
  repetitionCount: number;
  nextReviewAt: string | null;
  algorithmState: string;
  sessionNumber: number;
}
