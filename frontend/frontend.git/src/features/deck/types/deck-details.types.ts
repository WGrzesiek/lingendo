import type { WordDto } from "@/types/word";
import {
  DeckCategory,
  DeckDifficulty,
  Language,
} from "@/features/deck/types/deck.types";
import { DeckVisibility } from "@/features/deck/types/created-deck.types";

/**
 * Szczegóły decka - uniwersalne dla owner i enrolled
 */
export interface DeckDetails {
  id: string;
  name: string;
  description: string;
  category: DeckCategory;
  difficulty: DeckDifficulty;
  visibility: DeckVisibility;
  languageFrom?: Language;
  languageTo?: Language;
  wordCount: number;
  createdAt: string;
  updatedAt: string;
  createdBy: {
    id: string;
    username: string;
  };
  isOwner: boolean;
  isTeacher: boolean;
}

/**
 * Słówko w decku (czyste, bez statusów nauki)
 */
export interface DeckWord {
  id: string;
  word: string;
  translations: string[];
  sentences: WordDto["sentences"];
  sentencesAI: WordDto["sentencesAI"];
  createdAt?: string;
}

/**
 * Statystyki decka (tylko dla właściciela)
 */
export interface DeckStats {
  totalStudents: number;
  activeStudents: number;
  completedStudents: number;
}
