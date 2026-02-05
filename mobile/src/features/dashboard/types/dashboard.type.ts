export interface StudentStatistics {
  activeDecks: number;
  completedLessonsThisMonth: number;
  streakDays: number;
  totalPoints: number;
  pointsThisWeek: number;
}

export type StudentActivityType =
  | 'LESSON_COMPLETED'
  | 'SESSION_STARTED'
  | 'SESSION_COMPLETED'
  | 'LOGIN';

export interface StudentActivityItem {

  type: StudentActivityType;
  title: string;
  subtitle: string;
  points: number;
  eventTime: string;
}

export interface LeaderboardEntryDto {
  userId: string;
  rank: number;
  displayName: string;
  points: number;
  completedCourses: number;
}

export interface LeaderboardOverviewDto {
  top3: LeaderboardEntryDto[];
  you: LeaderboardEntryDto;
  aboveYou: LeaderboardEntryDto;
}

/**
 * Wpis w rankingu (UI) - uproszczony dla komponentów
 */
export interface LeaderboardEntry {
  rank: number;
  userId: number;
  username: string;
  points: number;
  isCurrentUser?: boolean;
}

/**
 * Talia/kurs (UI) - uproszczony dla komponentów
 */
export interface Deck {
  id: string;
  enrollmentId: string;
  name: string;
  description: string;
  totalCards: number;
  learnedCards: number;
  progress: number;
  lastStudied?: string;
}

