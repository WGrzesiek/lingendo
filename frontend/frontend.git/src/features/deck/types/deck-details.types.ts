import type { WordDto } from "@/types/word";

/**
 * Szczegóły decka - uniwersalne dla owner i enrolled
 */
export interface DeckDetails {
  id: string;
  name: string;
  description: string;
  category: string;
  difficulty: "BEGINNER" | "INTERMEDIATE" | "ADVANCED" | "EXPERT";
  visibility: "PUBLIC" | "PRIVATE" | "FRIENDS_ONLY" | "STUDENTS_ONLY";
  wordCount: number;
  createdAt: string;
  updatedAt: string;
  createdBy: {
    id: string;
    username: string;
  };
  /** Czy zalogowany użytkownik jest właścicielem */
  isOwner: boolean;
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
  createdAt: string;
}

/**
 * Statystyki decka (tylko dla właściciela)
 */
export interface DeckStats {
  totalStudents: number;
  activeStudents: number;
  completedStudents: number;
  averageProgress: number; // 0-100
  totalViews: number;
}
