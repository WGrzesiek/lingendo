/**
 * Statystyki nauki dla konkretnego kursu
 */
export interface ICourseStudyStatistics {
  /** ID zapisu na kurs */
  enrollmentId: string;
  /** Nazwa kursu */
  courseName: string;
  /** Całkowita liczba odpowiedzi */
  totalAnswers: number;
  /** Poprawne odpowiedzi */
  correctAnswers: number;
  /** Niepoprawne odpowiedzi */
  incorrectAnswers: number;
  /** Procent poprawnych odpowiedzi */
  accuracy: number;
  /** Średni czas odpowiedzi w sekundach */
  averageResponseTime: number;
  /** Ukończone sesje */
  completedSessions: number;
  /** Całkowity czas nauki w minutach */
  totalStudyTime: number;
  /** Najszybsza odpowiedź w sekundach */
  fastestResponse: number;
  /** Najwolniejsza odpowiedź w sekundach */
  slowestResponse: number;
  /** Data ostatniej sesji */
  lastSessionDate?: string;
}

/**
 * Statystyki dla pojedynczej fiszki w kursie
 */
export interface IFlashcardStatistics {
  /** ID fiszki */
  flashcardId: string;
  /** Słowo/pytanie */
  word: string;
  /** Tłumaczenie/odpowiedź */
  translation: string;
  /** Liczba odpowiedzi */
  totalAnswers: number;
  /** Poprawne odpowiedzi */
  correctAnswers: number;
  /** Celność (%) */
  accuracy: number;
  /** Średni czas odpowiedzi (s) */
  avgResponseTime: number;
}
