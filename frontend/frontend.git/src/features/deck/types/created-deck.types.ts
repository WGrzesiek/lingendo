import {DeckCategory, DeckDifficulty, DeckOwnerType, UpdateDeckVisibilityRequest} from "./deck.types";

/**
 * Statystyki kursu utworzonego przez użytkownika
 */
export interface CreatedDeckStats {
  enrolledUsers: number;
  averageRating: number | null;
  totalRatings: number;
  completions: number;
}

export type DeckVisibility = "PUBLIC" | "PRIVATE" | "FRIENDS_ONLY" | "STUDENTS_ONLY"
/**
 * Element listy kursów utworzonych przez użytkownika
 */
export interface ICreatedDeckListItem {
  id: string;
  name: string;
  deckDescription: string;
  deckDifficulty: DeckDifficulty;
  deckOwner: DeckOwnerType
  deckCategory: DeckCategory;
  ownerId: string;
  wordCount: number;
  visibility: DeckVisibility
  createdAt: string;
  updatedAt: string;

  stats?: CreatedDeckStats;
}

/**
 * Odpowiedź API z listą utworzonych kursów
 */
export interface CreatedDecksResponse {
  content: ICreatedDeckListItem[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}
export interface DeckStat {
  totalStudents: number;
  completedStudents: number;
}
export interface DecksStats{
    [deckId: string]: DeckStat;
}
