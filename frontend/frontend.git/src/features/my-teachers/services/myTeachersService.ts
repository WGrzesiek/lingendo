import apiClient from "@/lib/api/axios";
import type {
  Teacher,
  InvitationInfo,
  JoinTeacherRequest,
  PageResponse,
} from "../types";

const BASE_URL = "/v1/teacher-student";

/**
 * Serwis API dla operacji ucznia związanych z nauczycielami
 */
export const myTeachersService = {
  /**
   * Pobiera listę nauczycieli ucznia
   */
  getMyTeachers: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<Teacher>> => {
    const response = await apiClient.get<PageResponse<Teacher>>(
      `${BASE_URL}/my-teachers`,
      { params: { page, size } }
    );
    return response.data;
  },

  /**
   * Pobiera informacje o zaproszeniu (przed dołączeniem)
   */
  getInvitationInfo: async (code: string): Promise<InvitationInfo> => {
    const response = await apiClient.get<InvitationInfo>(
      `${BASE_URL}/invitations/${code}/info`
    );
    return response.data;
  },

  /**
   * Dołącza do nauczyciela za pomocą kodu zaproszenia
   */
  joinTeacher: async (invitationCode: string): Promise<Teacher> => {
    const request: JoinTeacherRequest = { invitationCode };
    const response = await apiClient.post<Teacher>(`${BASE_URL}/join`, request);
    return response.data;
  },

  /**
   * Opuszcza nauczyciela (usuwa relację)
   */
  leaveTeacher: async (teacherId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/my-teachers/${teacherId}`);
  },
};
