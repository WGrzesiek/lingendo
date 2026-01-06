/**
 * Status grupy
 */
export type GroupStatus = "ACTIVE" | "INACTIVE" | "ARCHIVED";

/**
 * Status członka grupy
 */
export type GroupMemberStatus = "ACTIVE" | "PENDING" | "INACTIVE";

/**
 * Grupa uczniów
 */
export interface Group {
  id: string;
  name: string;
  description?: string;
  teacherId: string;
  teacherName?: string;
  memberCount: number;
  sharedDecksCount: number;
  createdAt: string;
  updatedAt?: string;
  status: GroupStatus;
}

/**
 * Szczegóły grupy z dodatkowymi informacjami
 */
export interface GroupDetails extends Group {
  members: GroupMember[];
  sharedDecks: GroupSharedDeck[];
  stats?: GroupStats;
}

/**
 * Członek grupy
 */
export interface GroupMember {
  id: string;
  studentId: string;
  studentName: string;
  studentEmail?: string;
  avatarUrl?: string;
  joinedAt: string;
  status: GroupMemberStatus;
  isOwner?: boolean;
  lastActiveAt?: string;
  stats?: MemberStats;
}

/**
 * Statystyki członka grupy
 */
export interface MemberStats {
  totalSessions: number;
  totalWordsLearned: number;
  totalCorrectAnswers: number;
  totalAnswers: number;
  accuracy: number;
  totalPoints: number;
  completedCourses: number;
}

/**
 * Kurs udostępniony grupie
 */
export interface GroupSharedDeck {
  deckId: string;
  deckName: string;
  sharedAt: string;
  isActive: boolean;
  activeStudents?: number;
  avgProgress?: number;
}

/**
 * Statystyki grupy
 */
export interface GroupStats {
  memberCount: number;
  activeMembersCount: number;
  sharedCoursesCount: number;
  totalSessionsCompleted: number;
  avgProgress: number;
  totalPoints: number;
}

/**
 * DTO do tworzenia grupy
 */
export interface CreateGroupDto {
  name: string;
  description?: string;
}

/**
 * DTO do aktualizacji grupy
 */
export interface UpdateGroupDto {
  name?: string;
  description?: string;
}

/**
 * DTO do dodawania wielu członków
 */
export interface AddGroupMembersBatchDto {
  studentIds: string[];
}

/**
 * Odpowiedź batch operacji
 */
export interface BatchOperationResponse {
  success: string[];
  failed: string[];
  errors: string[];
  totalProcessed: number;
  successCount: number;
  failedCount: number;
}

// ============================================
// STATYSTYKI GRUPY (z statistics-service)
// ============================================

/**
 * Pełne statystyki grupy
 */
export interface GroupDashboardStats {
  totalMembers: number;
  activeMembers: number;
  sharedDecks: number;
  completedLessons: number;
  totalPoints: number;
  totalWordsLearned: number;
  totalStudyTimeMinutes: number;
  totalSessions: number;
  averageProgress: number;
  averageAccuracy: number;
  averageWordsPerDay: number;
}

/**
 * Członek grupy ze statystykami (TopMembers)
 */
export interface GroupMemberWithStats {
  studentId: string;
  studentName: string;
  email?: string;
  progress: number;
  coursesCompleted: number;
  totalPoints: number;
  lastActive?: string;
  trend: "up" | "down" | "stable";
}

/**
 * Kurs grupy ze statystykami
 */
export interface GroupCourseStats {
  deckId: string;
  deckName: string;
  studentsCount: number;
  lastActivity: string;
}

/**
 * Element aktywności grupy
 */
export interface GroupActivityItem {
  eventTime: string;
  studentId: string;
  studentName: string;
  activityType: "LESSON_COMPLETED" | "COURSE_STARTED" | "COURSE_COMPLETED";
  deckId: string;
  deckName: string;
}

/**
 * Wpis w rankingu grupy
 */
export interface GroupLeaderboardEntry {
  rank: number;
  studentId: string;
  studentName: string;
  correctAnswers: number;
  sessions: number;
  accuracy: number;
}

/**
 * Pełny dashboard grupy
 */
export interface GroupDashboard {
  stats: GroupDashboardStats;
  topMembers: GroupMemberWithStats[];
  sharedCourses: GroupCourseStats[];
  activityFeed: GroupActivityItem[];
}

/**
 * Podstawowe informacje o grupie
 */
export interface GroupInfo {
  id: string;
  name: string;
  description?: string;
  teacherId: string;
  memberCount: number;
  sharedDecksCount: number;
  createdAt: string;
  status: GroupStatus;
}

/**
 * Odpowiedź API dla członka grupy
 */
export interface GroupMemberApiResponse {
  id: string;
  studentId: string;
  username: string;
  firstName: string;
  lastName: string;
  email: string;
  status: GroupMemberStatus;
  joinedAt: string;
}