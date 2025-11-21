/**
 * Typy związane z taliami (decks)
 */

import type { DeckOwnerType, Language, LearnAlgorithm } from "@/types/common";

/**
 * DTO talii - podstawowe informacje
 */
export interface DeckDto {
  id: string;
  name: string;
  isPublic: boolean;
  userId: string;
  ownerType: string;
  wordCount: number;
}

/**
 * DTO szczegółów talii
 */
export interface DeckDetailsDto {
  id: string;
  userId: string;
  wordCount: number;
  createdAt: string;
  updatedAt: string;
  name: string;
  description: string;
  isPublic: boolean;
  owner: DeckOwnerType;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  languageFrom: Language;
  languageTo: Language;
}

/**
 * Request do utworzenia nowej talii
 */
export interface CreateDeckDto {
  deckName: string;
  description: string;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  languageFrom: Language;
  languageTo: Language;
  owner: DeckOwnerType;
  isPublic: boolean;
}

/**
 * Request do aktualizacji szczegółów talii
 */
export interface UpdateDeckDetailsRequest {
  id: string;
  userId: string;
  wordCount: number;
  createdAt: string;
  updatedAt: string;
  name: string;
  description: string;
  isPublic: boolean;
  owner: DeckOwnerType;
  learnAlgorithm: LearnAlgorithm;
  howManyFlashcardsForOneSession: number;
  languageFrom: Language;
  languageTo: Language;
}

/**
 * Response po utworzeniu talii
 */
export interface ResponseDeckDto {
  deckName: string;
  message: string;
}

/**
 * Request do aktualizacji widoczności talii
 */
export interface UpdateDeckVisibilityRequest {
  isPublic: boolean;
}

/**
 * Request do aktualizacji właściciela talii
 */
export interface UpdateDeckOwnerRequest {
  newOwner: DeckOwnerType;
}

/**
 * Request do aktualizacji nazwy talii
 */
export interface UpdateDeckNameRequest {
  deckName: string;
}

/**
 * Request do aktualizacji algorytmu nauki
 */
export interface UpdateLearnAlgorithmRequest {
  learnAlgorithm: LearnAlgorithm;
}

/**
 * Request do aktualizacji liczby fiszek na sesję
 */
export interface UpdateFlashcardsPerSessionRequest {
  flashcardsPerSession: number;
}

/**
 * Statystyki talii
 */
export interface DeckStatisticsDto {
  deckId: string;
  deckName: string;
  totalFlashcards: number;
  learnedFlashcards: number;
  unlearnedFlashcards: number;
  progressPercentage: number;
  totalSessions: number;
  completedSessions: number;
}

/**
 * Liczba talii użytkownika
 */
export interface UserDeckCountDto {
  userId: string;
  totalDecks: number;
  publicDecks: number;
  privateDecks: number;
}

export interface StudentMyCourseListItem {
  deckId: string;
  deckName: string;
  deckDescription: string;
  totalSession: number;
  learnedSession: number;
  progressPercentage: number | null;
  lastAccessed: string | null;
  deckDifficulty: DeckDifficulty;
}

export type DeckDifficulty = "EASY" | "MEDIUM" | "HARD";

// Do wyniesienia kiedys do glownych typow
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
