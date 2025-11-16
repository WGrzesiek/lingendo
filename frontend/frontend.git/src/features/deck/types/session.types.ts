/**
 * Typy związane z sesjami nauki (learning sessions)
 */

import type { SessionStatus, SessionType } from "@/types/common";
import type { WordDto } from "@/types/word";

/**
 * DTO sesji nauki - podstawowe informacje
 */
export interface SessionDto {
  id: string;
  deckId: string;
  userId: string;
  totalFlashcards: number;
  correctAnswers: number;
  wrongAnswers: number;
  skipped: number;
  durationSeconds: number;
  completedAt: string;
  status: SessionStatus;
  type: SessionType;
  createdAt: string;
  updatedAt: string;
}

/**
 * DTO szczegółów sesji nauki
 */
export interface SessionDetailDto {
  id: string;
  deckId: string;
  deckName: string;
  userId: string;
  totalFlashcards: number;
  correctAnswers: number;
  wrongAnswers: number;
  skipped: number;
  durationSeconds: number;
  accuracy: number;
  progress: number;
  completedAt: string;
  status: SessionStatus;
  type: SessionType;
  createdAt: string;
  updatedAt: string;
}

/**
 * Statystyki sesji nauki
 */
export interface SessionStatsDto {
  sessionId: string;
  totalFlashcards: number;
  answeredFlashcards: number;
  correctAnswers: number;
  wrongAnswers: number;
  skipped: number;
  accuracyPercentage: number;
  progressPercentage: number;
  averageTimePerFlashcard: number;
}

/**
 * DTO fiszki w sesji nauki
 */
export interface SessionFlashcardDto {
  id: string;
  flashcardId: string;
  wordDto: WordDto;
  correctAnswers: number;
  totalAttempts: number;
  isLearned: boolean;
  isSkipped: boolean;
  answeredInSession: boolean;
  wasCorrect: boolean;
  addedToSessionAt: string;
}
