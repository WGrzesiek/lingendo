import type {
  DeckDifficulty,
  DeckCategory,
} from "@/features/deck/types/deck.types";

/**
 * Status relacji nauczyciel-student
 */
export type TeacherStudentStatus = "ACTIVE" | "BLOCKED" | "REMOVED";

/**
 * Status zaproszenia
 */
export type InvitationStatus = "ACTIVE" | "USED" | "EXPIRED" | "REVOKED";

/**
 * Student przypisany do nauczyciela
 */
export interface Student {
  id: string;
  userId: string;
  username: string;
  email?: string;
  avatarUrl?: string;
  joinedAt: string;
  status: TeacherStudentStatus;
  lastActiveAt?: string;
  stats: StudentStats;
}

/**
 * Statystyki studenta
 */
export interface StudentStats {
  totalSessions: number;
  totalCorrectAnswers: number;
  totalAnswers: number;
  accuracy: number;
  totalPoints: number;
  streakDays: number;
  lastSessionAt?: string;
}

/**
 * Zaproszenie nauczyciela
 */
export interface TeacherInvitation {
  id: string;
  code: string;
  teacherId: string;
  maxUses: number;
  currentUses: number;
  expiresAt: string;
  status: InvitationStatus;
  createdAt: string;
  description?: string;
}

/**
 * DTO do tworzenia zaproszenia
 */
export interface CreateInvitationDto {
  maxUses?: number;
  expiresInDays?: number;
  description?: string;
}

/**
 * Widoczność talii (lokalna wersja z SHARED)
 */
export type DeckVisibility = "PUBLIC" | "PRIVATE" | "SHARED";

/**
 * Typ właściciela talii dla dashboardu
 */
export type DeckOwner = "USER" | "ADMIN" | "SYSTEM";

/**
 * Kurs (talia) nauczyciela - odpowiada DeckDto z backendu
 */
export interface TeacherCourse {
  id: string;
  name: string;
  deckDescription?: string;
  deckDifficulty?: DeckDifficulty;
  deckOwner?: DeckOwner;
  deckCategory?: DeckCategory;
  ownerId: string;
  wordCount: number;
  visibility: DeckVisibility;
  createdAt: string;
  updatedAt?: string;
  username?: string;
  /** Czy kurs jest udostępniony studentom (obliczane na froncie) */
  isShared: boolean;
}

/**
 * Statystyki dashboardu nauczyciela
 */
export interface TeacherDashboardStats {
  totalStudents: number;
  activeStudents: number;
  totalCourses: number;
  sharedCourses: number;
  totalSessions: number;
  averageAccuracy: number;
  weeklyGrowth: number;
}

/**
 * Aktywność w feedzie
 */
export interface ActivityItem {
  id: string;
  type:
    | "SESSION_COMPLETED"
    | "STUDENT_JOINED"
    | "COURSE_SHARED"
    | "ACHIEVEMENT";
  studentId?: string;
  studentName?: string;
  courseId?: string;
  courseName?: string;
  description: string;
  timestamp: string;
  metadata?: Record<string, unknown>;
}

/**
 * Filtry dla listy studentów
 */
export interface StudentFilters {
  search?: string;
  status?: TeacherStudentStatus | "ALL";
  sortBy?: "joinedAt" | "lastActiveAt" | "points" | "accuracy";
  sortOrder?: "asc" | "desc";
}

/**
 * Odpowiedź paginowana
 */
export interface PaginatedResponse<T> {
  data: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

/**
 * Top student z API statystyk
 */
export interface TopStudent {
  studentId: string;
  studentName: string;
  totalPoints: number;
  lastActive: string;
}

/**
 * Szczegółowe statystyki nauczyciela
 */
export interface TeacherStatsDetails {
  createdDecks: number;
  createdFlashcards: number;
  totalStudentPoints: number;
  totalStudentSessions: number;
  averageAccuracy: number;
  activeStudents: number;
  totalStudents: number;
  totalCorrectAnswers: number;
  totalAnswers: number;
  pointsPerMonth: Record<string, number>;
}

/**
 * Aktywność ucznia dla nauczyciela
 */
export interface TeacherActivityItem {
  eventTime: string;
  studentId: string;
  studentName: string;
  activityType: string;
  deckId: string;
  deckName: string;
}

// Re-eksport typów z deck
export type { DeckDifficulty, DeckCategory };
