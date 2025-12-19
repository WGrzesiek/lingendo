/**
 * Pozycja użytkownika w rankingu
 */
export interface ILeaderboardEntry {
  /** ID użytkownika */
  userId: string;
  /** Pozycja w rankingu */
  rank: number;
  /** Nazwa wyświetlana użytkownika */
  displayName: string;
  /** Liczba punktów */
  points: number;
  /** Liczba ukończonych kursów */
  completedCourses: number;
  /** Czy użytkownik jest aktywny */
  isActive?: boolean;
}

/**
 * Pełny ranking użytkowników
 */
export interface IFullLeaderboard {
  /** Lista wszystkich pozycji w rankingu */
  entries: ILeaderboardEntry[];
  /** Pozycja zalogowanego użytkownika */
  currentUser: ILeaderboardEntry;
  /** Użytkownik powyżej zalogowanego (jeśli istnieje) */
  userAbove?: ILeaderboardEntry;
  /** Całkowita liczba użytkowników w rankingu */
  totalUsers: number;
}

/**
 * Filtry dla rankingu
 */
export interface ILeaderboardFilters {
  /** Wyszukiwanie po nazwie użytkownika */
  search?: string;
}
