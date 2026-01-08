import type { DeckCategory, DeckDifficulty } from "@/features/deck/types/deck.types";
import type { DeckVisibility } from "@/features/deck/types/created-deck.types";

/**
 * Kurs społeczności - mapowanie z ICreatedDeckListItem
 */
export interface ICommunityCourse {
  id: string;
  title: string;
  description: string;
  wordCount: number;
  difficulty: DeckDifficulty;
  category: DeckCategory;
  visibility: DeckVisibility;
  createdAt: string;
  updatedAt: string;
}

/**
 * Filtry dla kursów społeczności
 */
export interface ICommunityCoursesFilters {
  search?: string;
  category?: string;
  difficulty?: DeckDifficulty;
  sortBy?: "newest" | "oldest";
}

/**
 * Odpowiedź API z listą kursów społeczności
 */
export interface CommunityCoursesResponse {
  content: ICommunityCourse[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
}
