/**
 * Pozycja użytkownika w rankingu
 */
export interface ILeaderboardEntry {
  userId: string;
  rank: number;
  displayName: string;
  points: number;
  completedCourses: number;
  isActive?: boolean;
}

/**
 * Pełny ranking użytkowników
 */
export interface IFullLeaderboard {
  entries: ILeaderboardEntry[];
  currentUser: ILeaderboardEntry;
  userAbove?: ILeaderboardEntry;
  totalUsers: number;
}

/**
 * Filtry dla rankingu
 */
export interface ILeaderboardFilters {
  search?: string;
}
