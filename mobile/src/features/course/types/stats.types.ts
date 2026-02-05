/**
 * Statystyki odpowiedzi dla fiszek w kursie
 */
export interface FlashcardAnswersStats {
  enrollmentId: string;
  totalAnswers: number;
  correctAnswers: number;
  incorrectAnswers: number;
  accuracy: number;
  averageResponseTime: number;
  totalStudyTime: number;
  fastestResponse: number;
  slowestResponse: number;
  lastSessionDate: number;
  until30SecAnswers: number;
}

/**
 * Statystyki nauki dla konkretnego kursu
 */
export interface CourseStudyStatistics {
  enrollmentId: string;
  courseName: string;
  totalAnswers: number;
  correctAnswers: number;
  incorrectAnswers: number;
  accuracy: number;
  averageResponseTime: number;
  completedSessions: number;
  totalStudyTime: number;
  fastestResponse: number;
  slowestResponse: number;
  lastSessionDate?: string;
}

/**
 * Statystyki dla pojedynczej fiszki w kursie
 */
export interface FlashcardStatistics {
  flashcardId: string;
  word: string;
  translation: string;
  totalAnswers: number;
  correctAnswers: number;
  accuracy: number;
  avgResponseTime: number;
}
