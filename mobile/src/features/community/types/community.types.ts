import type {
  DeckCategory,
  DeckDifficulty,
  DeckVisibility,
} from '@/features/deck/types/deck.types';
import type { Language } from '@/types/common';


/**
 * Filtry dla kursów społeczności
 */
export interface CommunityCoursesFilters {
  search?: string;
  category?: DeckCategory;
  difficulty?: DeckDifficulty;
  sortBy?: 'newest' | 'oldest';
}

/**
 * Item talii
 */
export interface PublicDeckItem {
  id: string;
  name: string;
  deckDescription: string;
  wordCount: number;
  deckDifficulty: DeckDifficulty;
  deckCategory: DeckCategory;
  visibility: DeckVisibility;
  languageFrom?: Language;
  languageTo?: Language;
  createdAt: string;
  updatedAt: string;
}
