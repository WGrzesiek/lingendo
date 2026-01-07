/**
 * Status relacji nauczyciel-uczeń
 */
export type TeacherStudentStatus = "ACTIVE" | "BLOCKED" | "REMOVED";

/**
 * Status zaproszenia
 */
export type InvitationStatus = "ACTIVE" | "USED" | "EXPIRED" | "REVOKED";

/**
 * Dane nauczyciela z perspektywy ucznia
 */
export interface Teacher {
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
 * Informacje o zaproszeniu (publiczne, przed dołączeniem)
 */
export interface InvitationInfo {
  id: string;
  invitationCode: string;
  invitationUrl: string;
  name: string;
  maxUses: number | null;
  currentUses: number;
  status: InvitationStatus;
  expiresAt: string | null;
  createdAt: string;
  teacherName?: string;
}

/**
 * Request do dołączenia do nauczyciela
 */
export interface JoinTeacherRequest {
  invitationCode: string;
}

/**
 * Standardowa paginowana odpowiedź
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
