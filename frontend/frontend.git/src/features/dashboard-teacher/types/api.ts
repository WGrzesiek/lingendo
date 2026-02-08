/**
 * Status relacji nauczyciel-student
 */
export type TeacherStudentStatus = "ACTIVE" | "BLOCKED" | "REMOVED";

/**
 * Status zaproszenia
 */
export type InvitationStatus = "ACTIVE" | "USED" | "EXPIRED" | "REVOKED";

/**
 * Paginowana odpowiedź ze Spring Boot
 */
export interface PageResponse<T> {
  content: T[];
  pageable: {
    pageNumber: number;
    pageSize: number;
    sort: {
      sorted: boolean;
      empty: boolean;
    };
  };
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * Dane studenta przypisanego do nauczyciela
 */
export interface StudentResponse {
  relationId: string;
  studentId: string;
  username: string;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  status: TeacherStudentStatus;
  joinedAt: string; // ISO datetime
}

/**
 * Dane zaproszenia nauczyciela
 */
export interface InvitationResponse {
  id: string;
  invitationCode: string;
  invitationUrl: string;
  name: string | null;
  maxUses: number | null;
  currentUses: number;
  status: InvitationStatus;
  expiresAt: string | null;
  createdAt: string;
}

/**
 * DTO do tworzenia zaproszenia
 */
export interface CreateInvitationRequest {
  name?: string;
  maxUses?: number;
  expiresAt?: string;
}

/**
 * Statystyki nauczyciela
 */
export interface TeacherStatsResponse {
  activeStudents: number;
  invitedStudents: number;
  blockedStudents: number;
  activeInvitations: number;
  totalInvitations: number;
}

/**
 * Odpowiedź z danymi nauczyciela
 */
export interface TeacherResponse {
  relationId: string;
  teacherId: string;
  username: string;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  status: TeacherStudentStatus;
  joinedAt: string;
}

/**
 * Odpowiedź z danymi grupy
 */
export interface GroupResponse {
  id: string;
  name: string;
  description: string | null;
  color: string | null;
  teacherId: string;
  memberCount: number;
  isArchived: boolean;
  createdAt: string;
  updatedAt: string;
}

/**
 * DTO do tworzenia grupy
 */
export interface CreateGroupRequest {
  name: string;
  description?: string;
  color?: string;
}

/**
 * DTO do aktualizacji grupy
 */
export interface UpdateGroupRequest {
  name?: string;
  description?: string;
  color?: string;
}

/**
 * Odpowiedź z danymi członka grupy
 */
export interface GroupMemberResponse {
  membershipId: string;
  studentId: string;
  username: string;
  firstName: string | null;
  lastName: string | null;
  email: string | null;
  joinedAt: string;
}

/**
 * DTO do dodawania członków
 */
export interface AddMembersRequest {
  studentIds: string[];
}

/**
 * DTO do usuwania członków
 */
export interface RemoveMembersRequest {
  studentIds: string[];
}

/**
 * Odpowiedź z operacji batch na członkach
 */
export interface BatchMemberOperationResponse {
  successful: string[];
  failed: string[];
  errors: Record<string, string>;
  totalProcessed: number;
  successCount: number;
  failedCount: number;
}

/**
 * Statystyki grup (GroupStatsResponse z backendu)
 */
export interface GroupStatsResponse {
  totalGroups: number;
  activeGroups: number;
  archivedGroups: number;
  totalMembers: number;
  averageMembersPerGroup: number;
}

/**
 * Typ celu udostępnienia
 */
export type ShareTargetType = "USER" | "GROUP" | "ALL_STUDENTS" | "ALL_FRIENDS";

/**
 * Status udostępnienia
 */
export type ShareStatus = "ACTIVE" | "EXPIRED" | "REVOKED";

/**
 * Odpowiedź z danymi udostępnienia
 */
export interface DeckShareResponse {
  id: string;
  deckId: string;
  deckName: string;
  sharedByUserId: string;
  targetType: ShareTargetType;
  targetId: string | null;
  targetName: string | null;
  status: ShareStatus;
  message: string | null;
  sharedAt: string;
  expiresAt: string | null;
}

/**
 * DTO do udostępniania talii
 */
export interface ShareDeckRequestBody {
  targetType: ShareTargetType;
  targetId?: string;
  message?: string;
  expiresAt?: string;
}

/**
 * DTO do batch udostępniania
 */
export interface BatchShareDeckRequestBody {
  targetType: ShareTargetType;
  targetIds: string[];
  message?: string;
  expiresAt?: string;
}

/**
 * Odpowiedź z batch udostępnienia
 */
export interface BatchShareResponse {
  success: string[];
  failed: string[];
  errors: string[];
  totalProcessed: number;
  successCount: number;
  failedCount: number;
}
