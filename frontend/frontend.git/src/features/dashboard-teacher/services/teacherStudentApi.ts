import apiClient from "@/lib/api/axios";
import type {
  StudentResponse,
  InvitationResponse,
  CreateInvitationRequest,
  TeacherStatsResponse,
  PageResponse,
} from "../types/api";
import type {
  TopStudent,
  TeacherStatsDetails,
  TeacherActivityItem,
} from "../types";


const BASE_URL = "/v1/teacher-student";
const STATS_BASE_URL = "/v1/dashboard/teacher";

export const teacherStudentApi = {

  /**
   * Pobiera listę zaproszeń nauczyciela
   */
  getInvitations: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<InvitationResponse>> => {
    const response = await apiClient.get<PageResponse<InvitationResponse>>(
      `${BASE_URL}/invitations`,
      { params: { page, size } }
    );
    return response.data;
  },

  /**
   * Tworzy nowe zaproszenie
   */
  createInvitation: async (
    request: CreateInvitationRequest
  ): Promise<InvitationResponse> => {
    const response = await apiClient.post<InvitationResponse>(
      `${BASE_URL}/invitations`,
      request
    );
    return response.data;
  },

  /**
   * Dezaktywuje zaproszenie
   */
  deactivateInvitation: async (invitationId: string): Promise<void> => {
    await apiClient.patch(`${BASE_URL}/invitations/${invitationId}/deactivate`);
  },

  /**
   * Usuwa zaproszenie
   */
  deleteInvitation: async (invitationId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/invitations/${invitationId}`);
  },

  /**
   * Pobiera publiczne informacje o zaproszeniu
   */
  getInvitationInfo: async (code: string): Promise<InvitationResponse> => {
    const response = await apiClient.get<InvitationResponse>(
      `${BASE_URL}/invitations/${code}/info`
    );
    return response.data;
  },

  // ==================== UCZNIOWIE ====================

  /**
   * Pobiera listę uczniów nauczyciela
   */
  getStudents: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<StudentResponse>> => {
    const response = await apiClient.get<PageResponse<StudentResponse>>(
      `${BASE_URL}/students`,
      { params: { page, size } }
    );
    return response.data;
  },

  /**
   * Usuwa ucznia z listy nauczyciela
   */
  removeStudent: async (studentId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/students/${studentId}`);
  },

  /**
   * Blokuje ucznia
   */
  blockStudent: async (studentId: string): Promise<void> => {
    await apiClient.patch(`${BASE_URL}/students/${studentId}/block`);
  },

  /**
   * Odblokowuje ucznia
   */
  unblockStudent: async (studentId: string): Promise<void> => {
    await apiClient.patch(`${BASE_URL}/students/${studentId}/unblock`);
  },

  // ==================== STATYSTYKI ====================

  /**
   * Pobiera statystyki nauczyciela
   */
  getTeacherStats: async (): Promise<TeacherStatsResponse> => {
    const response = await apiClient.get<TeacherStatsResponse>(
      `${BASE_URL}/stats`
    );
    return response.data;
  },

  // ==================== SPRAWDZANIE ====================

  /**
   * Sprawdza czy istnieje relacja nauczyciel-uczeń
   */
  isTeacherOf: async (
    teacherId: string,
    studentId: string
  ): Promise<boolean> => {
    const response = await apiClient.get<boolean>(
      `${BASE_URL}/check/${teacherId}/${studentId}`
    );
    return response.data;
  },
};

export const studentTeacherApi = {
  /**
   * Dołącza do nauczyciela za pomocą kodu zaproszenia
   */
  joinTeacher: async (
    invitationCode: string
  ): Promise<{ relationId: string; teacherId: string }> => {
    const response = await apiClient.post(`${BASE_URL}/join`, {
      invitationCode,
    });
    return response.data;
  },

  /**
   * Pobiera listę nauczycieli ucznia
   */
  getMyTeachers: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<StudentResponse>> => {
    const response = await apiClient.get<PageResponse<StudentResponse>>(
      `${BASE_URL}/my-teachers`,
      { params: { page, size } }
    );
    return response.data;
  },

  /**
   * Opuszcza nauczyciela
   */
  leaveTeacher: async (teacherId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/my-teachers/${teacherId}`);
  },
};


export const teacherDashboardStatsApi = {
  /**
   * Pobiera najlepszych uczniów
   */
  getTopStudents: async (limit: number = 5): Promise<TopStudent[]> => {
    const response = await apiClient.get<TopStudent[]>(
      `${STATS_BASE_URL}/students/top`,
      { params: { limit } }
    );
    return response.data;
  },

  /**
   * Pobiera szczegółowe statystyki nauczyciela
   */
  getStatsDetails: async (): Promise<TeacherStatsDetails> => {
    const response = await apiClient.get<TeacherStatsDetails>(
      `${STATS_BASE_URL}/stats-details`
    );
    return response.data;
  },

  /**
   * Pobiera aktywność uczniów
   */
  getActivity: async (limit: number = 10): Promise<TeacherActivityItem[]> => {
    const response = await apiClient.get<TeacherActivityItem[]>(
      `${STATS_BASE_URL}/activity`,
      { params: { limit } }
    );
    return response.data;
  },
};
