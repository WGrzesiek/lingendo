/**
 * Statystyki nauki dla konkretnego kursu
 */
export interface ICourseStudyStatistics {

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
export interface IFlashcardStatistics {
  flashcardId: string;
  word: string;
  translation: string;
  totalAnswers: number;
  correctAnswers: number;
  accuracy: number;
  avgResponseTime: number;
}
