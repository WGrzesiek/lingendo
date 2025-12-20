/**
 * Główne statystyki użytkownika
 */
export interface IUserStatistics {
  /** Całkowita liczba punktów */
  totalPoints: number;
  /** Aktualny streak (dni z rzędu) */
  currentStreak: number;
  /** Ukończone kursy */
  finishedDecks: number;
  /** Utworzone kursy */
  createdDecks: number;
  /** Utworzone fiszki */
  createdFlashcards: number;
  /** Zapisane kursy */
  enrolledDecks: number;
  /** Ukończone sesje */
  completedSessions: number;
  /** Celność odpowiedzi (%) */
  accuracy: number;
}

/**
 * Punkty użytkownika w danym okresie
 */
export interface IUserPointsData {
  /** Data (dzień lub miesiąc) */
  date: string;
  /** Liczba punktów */
  points: number;
}

/**
 * Statystyki sesji nauki
 */
export interface ISessionStatistics {
  /** Całkowita liczba rozpoczętych sesji */
  totalSessionsStarted: number;
  /** Całkowita liczba ukończonych sesji */
  totalSessionsFinished: number;
  /** Poprawne odpowiedzi */
  totalCorrectAnswers: number;
  /** Niepoprawne odpowiedzi */
  totalIncorrectAnswers: number;
  /** Celność (%) */
  accuracy: number;
  /** Średnia poprawnych odpowiedzi na sesję */
  avgCorrectPerSession: number;
}

/**
 * Wpis aktywności użytkownika (z user_activity)
 */
export interface IUserActivityItem {
  /** Czas zdarzenia */
  eventTime: string;
  /** Typ aktywności */
  type: "LESSON_COMPLETED" | "SESSION_STARTED" | "SESSION_COMPLETED" | "LOGIN";
  /** Tytuł aktywności */
  title: string;
  /** Podtytuł aktywności */
  subtitle: string;
  /** Punkty zdobyte za aktywność */
  points: number;
}

/**
 * Statystyki dla konkretnego kursu
 */
export interface IDeckStatistics {
  /** ID kursu */
  deckId: string;
  /** Nazwa kursu */
  deckName: string;
  /** Liczba ukończonych sesji */
  completedSessions: number;
  /** Poprawne odpowiedzi */
  correctAnswers: number;
  /** Niepoprawne odpowiedzi */
  incorrectAnswers: number;
  /** Celność (%) */
  accuracy: number;
  /** Ostatnia aktywność */
  lastActivity: string;
}

/**
 * Pełne statystyki użytkownika
 */
export interface IFullUserStatistics {
  /** Podstawowe statystyki */
  overview: IUserStatistics;
  /** Punkty dzienne (ostatnie 30 dni) */
  dailyPoints: IUserPointsData[];
  /** Punkty miesięczne (ostatnie 12 miesięcy) */
  monthlyPoints: IUserPointsData[];
  /** Statystyki sesji */
  sessions: ISessionStatistics;
  /** Historia aktywności */
  recentActivity: IUserActivityItem[];
  /** Statystyki dla kursów */
  deckStats: IDeckStatistics[];
}

/**
 * Statystyki użytkownika z API /api/v1/stats
 */
export interface IStatisticsApiResponse {
  /** Liczba utworzonych kursów */
  createdDecks: number;
  /** Średnia liczba odpowiedzi na sesję */
  averageAnswersPerSession: number;
  /** Liczba utworzonych fiszek */
  flashcardsCreated: number;
  /** Całkowita liczba punktów */
  totalPoints: number;
  /** Liczba poprawnych odpowiedzi na fiszki */
  flashcardsAnsweredCorrectly: number;
  /** Liczba ukończonych sesji */
  sessionsCompleted: number;
  /** Aktualny streak (dni z rzędu) */
  streak: number;
  /** Punkty pogrupowane po miesiącach (YYYYMM: punkty) */
  pointsPerMonth: Record<string, number>;
  /** Liczba zapisanych kursów */
  enrolledDecks: number;
  /** Liczba ukończonych kursów */
  completedDecks: number;
  /** Całkowita liczba odpowiedzi na fiszki */
  flashcardsAnswered: number;
}
