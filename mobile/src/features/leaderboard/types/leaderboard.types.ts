/**
 * Pozycja użytkownika w rankingu (z API)
 */
export interface LeaderboardEntryDto {
  userId: string;
  rank: number;
  displayName: string;
  points: number;
  completedCourses: number;
  isActive?: boolean;
}

/**
 * Pełny ranking użytkowników (z API)
 */
export interface FullLeaderboardDto {
  entries: LeaderboardEntryDto[];
  currentUser: LeaderboardEntryDto;
  userAbove?: LeaderboardEntryDto;
  totalUsers: number;
}

/**
 * Filtry dla rankingu
 */
export interface LeaderboardFilters {
  search?: string;
}
