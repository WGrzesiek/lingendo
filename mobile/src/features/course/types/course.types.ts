import type { Language } from '@/types/common';
import type { DeckOwnerType, ReviewSchedule } from '@/features/deck/types';

export type Visibility = 'PUBLIC' | 'PRIVATE';

export const sessionStatusValues = ['NEW', 'IN_PROGRESS', 'COMPLETED', 'SKIPPED'] as const;
export type SessionStatus = (typeof sessionStatusValues)[number];

/**
 * Nagłówek kursu - podstawowe informacje
 */
export interface CourseHeader {
  deckId: string;
  name: string;
  description: string;
  ownerId: string;
  username: string;
  ownerType: DeckOwnerType;
  visibility: Visibility;
  languageFrom: Language;
  languageTo: Language;
}

/**
 * Informacje o pojedynczej sesji nauki
 */
export interface SessionInfo {
  sessionId: string;
  sessionNumber: number;
  status: SessionStatus;
}

/**
 * Postęp kursu - ukończone sesje, słówka do powtórki
 */
export interface CourseProgress {
  completedSessions: number;
  totalSessions: number;
  wordsPerSession: number;
  totalWords: number;
  wordsToReview: number;
  nextReviewDate?: string;
  sessions: SessionInfo[];
}

/**
 * Ustawienia kursu (enrollment)
 */
export interface CourseSettings {
  enrollmentId: string;
  algorithm: string;
  wordsPerSession: number;
  reviewSchedule: ReviewSchedule;
}

/**
 * Odpowiedź inicjalizacji sesji
 */
export interface InitializeSessionResponse {
  sessionId: string;
}
