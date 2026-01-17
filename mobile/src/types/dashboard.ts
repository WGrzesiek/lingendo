/**
 * Statystyki ucznia
 */
export interface StudentStatistics {
  activeDecks: number;
  completedLessonsThisMonth: number;
  streakDays: number;
  totalPoints: number;
  pointsThisWeek: number;
}

/**
 * Kurs/talia
 */
export interface Deck {
  id: number;
  name: string;
  description: string;
  totalCards: number;
  learnedCards: number;
  progress: number;
  lastStudied?: string;
}

/**
 * Pozycja w rankingu
 */
export interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  points: number;
  isCurrentUser?: boolean;
}

/**
 * Ostatnia aktywność
 */
export interface RecentActivityItem {
  id: number;
  type: 'lesson_completed' | 'deck_started' | 'achievement_earned' | 'streak_reached';
  title: string;
  description: string;
  timestamp: string;
}
