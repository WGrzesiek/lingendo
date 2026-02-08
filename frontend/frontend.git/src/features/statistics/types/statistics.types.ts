import {IUserActivityItem} from "@/common/userActivity";

/**
 * Główne statystyki użytkownika
 */
export interface IUserStatistics {
  totalPoints: number;
  currentStreak: number;
  finishedDecks: number;
  createdDecks: number;
  createdFlashcards: number;
  enrolledDecks: number;
  completedSessions: number;
  accuracy: number;
}

/**
 * Punkty użytkownika w danym okresie
 */
export interface IUserPointsData {
  date: string;
  points: number;
}

/**
 * Statystyki sesji nauki
 */
export interface ISessionStatistics {
  totalSessionsStarted: number;
  totalSessionsFinished: number;
  totalCorrectAnswers: number;
  totalIncorrectAnswers: number;
  accuracy: number;
  avgCorrectPerSession: number;
}

/**
 * Statystyki dla konkretnego kursu
 */
export interface IDeckStatistics {
  deckId: string;
  deckName: string;
  completedSessions: number;
  correctAnswers: number;
  incorrectAnswers: number;
  accuracy: number;
  lastActivity: string;
}

/**
 * Pełne statystyki użytkownika
 */
export interface IFullUserStatistics {
  overview: IUserStatistics;
  dailyPoints: IUserPointsData[];
  monthlyPoints: IUserPointsData[];
  sessions: ISessionStatistics;
  recentActivity: IUserActivityItem[];
  deckStats: IDeckStatistics[];
}

/**
 * Statystyki użytkownika z API /api/v1/stats
 */
export interface IStatisticsApiResponse {
  createdDecks: number;
  averageAnswersPerSession: number;
  flashcardsCreated: number;
  totalPoints: number;
  flashcardsAnsweredCorrectly: number;
  sessionsCompleted: number;
  streak: number;
  pointsPerMonth: Record<string, number>;
  enrolledDecks: number;
  completedDecks: number;
  flashcardsAnswered: number;
}
