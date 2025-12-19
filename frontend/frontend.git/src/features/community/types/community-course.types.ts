/**
 * Kurs społeczności na stronie przeglądania
 */
export interface ICommunityCourse {
  /** ID kursu */
  id: string;
  /** Nazwa kursu */
  title: string;
  /** Opis kursu */
  description: string;
  /** Autor kursu */
  author: string;
  /** Liczba zapisanych uczniów */
  studentsCount: number;
  /** Średnia ocena kursu (1-5) */
  rating: number;
  /** Liczba ocen */
  ratingsCount: number;
  /** Liczba lekcji */
  lessonsCount: number;
  /** Poziom trudności */
  difficulty: "EASY" | "MEDIUM" | "HARD";
  /** Kategoria kursu */
  category: string;
  /** Data utworzenia */
  createdAt: string;
  /** Data ostatniej aktualizacji */
  updatedAt: string;
  /** Liczba słówek w kursie */
  totalWords: number;
}

/**
 * Filtry dla kursów społeczności
 */
export interface ICommunityCoursesFilters {
  /** Wyszukiwanie po tytule */
  search?: string;
  /** Kategoria kursu */
  category?: string;
  /** Poziom trudności */
  difficulty?: "EASY" | "MEDIUM" | "HARD";
  /** Sortowanie */
  sortBy?: "popular" | "rating" | "newest" | "oldest";
}

/**
 * Odpowiedź API z listą kursów społeczności
 */
export interface CommunityCoursesResponse {
  content: ICommunityCourse[];
  totalElements: number;
  totalPages: number;
  currentPage: number;
}
