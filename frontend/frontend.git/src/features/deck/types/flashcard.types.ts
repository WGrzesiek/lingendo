/**
 * Typy związane z fiszkami (flashcards)
 */

import type { WordDto } from "@/types/word";

/**
 * DTO fiszki
 */
export interface FlashcardDto {
  id: string;
  wordDto: WordDto;
  correctAnswers: number;
  totalAttempts: number;
  isLearned: boolean;
  isSkipped: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * Request do zapisania odpowiedzi na fiszkę
 */
export interface RecordAnswerRequest {
  flashcardId: string;
  isCorrect: boolean;
}
