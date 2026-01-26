import apiClient from "@/lib/api/axios";
import type { PageResponse } from "@/types/common";
import type {
  Group,
  GroupDetails,
  CreateGroupDto,
  UpdateGroupDto,
  GroupMember,
  AddGroupMembersBatchDto,
  BatchOperationResponse,
  GroupDashboard,
  GroupCourseStats,
  GroupActivityItem,
  GroupLeaderboardEntry,
  GroupInfo,
  GroupMemberApiResponse
} from "../types/group.types";

const GROUPS_BASE_URL = "/v1/groups";
const STATS_BASE_URL = "/v1/stats/groups";


const mapApiResponseToGroupMember = (
  apiMember: GroupMemberApiResponse
): GroupMember => ({
  id: apiMember.id,
  studentId: apiMember.studentId,
  studentName:
    `${apiMember.firstName} ${apiMember.lastName}`.trim() || apiMember.username,
  studentEmail: apiMember.email,
  joinedAt: apiMember.joinedAt,
  status: apiMember.status,
});


// ============================================
// ZARZĄDZANIE GRUPAMI (user-service)
// ============================================

/**
 * Serwis do zarządzania grupami
 */
export const groupsService = {
  /**
   * Pobiera szczegóły grupy
   */
  async getGroupById(groupId: string): Promise<GroupDetails> {
    const response = await apiClient.get<GroupDetails>(
      `${GROUPS_BASE_URL}/${groupId}`
    );
    return response.data;
  },

  /**
   * Tworzy nową grupę
   */
  async createGroup(data: CreateGroupDto): Promise<Group> {
    const response = await apiClient.post<Group>(GROUPS_BASE_URL, data);
    return response.data;
  },

  /**
   * Aktualizuje grupę
   */
  async updateGroup(groupId: string, data: UpdateGroupDto): Promise<Group> {
    const response = await apiClient.put<Group>(
      `${GROUPS_BASE_URL}/${groupId}`,
      data
    );
    return response.data;
  },

  /**
   * Usuwa (archiwizuje) grupę
   */
  async deleteGroup(groupId: string): Promise<void> {
    await apiClient.delete(`${GROUPS_BASE_URL}/${groupId}`);
  },
};

// ============================================
// ZARZĄDZANIE CZŁONKAMI GRUPY
// ============================================

/**
 * Serwis do zarządzania członkami grupy
 */
export const groupMembersService = {
  /**
   * Pobiera członków grupy
   */
  async getMembers(
    groupId: string,
  ): Promise<GroupMember[]> {

    const response = await apiClient.get<PageResponse<GroupMemberApiResponse>>(
      `${GROUPS_BASE_URL}/${groupId}/members`
    );

    return response.data.content.map(mapApiResponseToGroupMember);
  },

  /**
   * Dodaje wielu członków do grupy
   */
  async addMembersBatch(
    groupId: string,
    data: AddGroupMembersBatchDto
  ): Promise<BatchOperationResponse> {
    const response = await apiClient.post<BatchOperationResponse>(
      `${GROUPS_BASE_URL}/${groupId}/members`,
      data
    );
    return response.data;
  },

  /**
   * Usuwa członków z grupy (jeden lub wielu)
   */
  async removeMembersBatch(
    groupId: string,
    studentIds: string[]
  ): Promise<BatchOperationResponse> {
    const response = await apiClient.delete<BatchOperationResponse>(
      `${GROUPS_BASE_URL}/${groupId}/members`,
      { data: { studentIds } }
    );
    return response.data;
  },
};

// ============================================
// STATYSTYKI GRUP (statistics-service)
// ============================================

/**
 * Serwis do pobierania statystyk grup
 */
export const groupStatsService = {
  /**
   * Pobiera listę grup ze statystykami
   */
  async getGroupsWithStats(): Promise<GroupInfo[]> {
    const response = await apiClient.get<GroupInfo[]>(STATS_BASE_URL);
    return response.data;
  },

  /**
   * Pobiera pełny dashboard grupy
   */
  async getGroupDashboard(groupId: string): Promise<GroupDashboard> {
    const response = await apiClient.get<GroupDashboard>(
      `${STATS_BASE_URL}/${groupId}/dashboard`
    );
    return response.data;
  },

  /**
   * Pobiera kursy udostępnione grupie
   */
  async getSharedCourses(
    groupId: string,
    limit: number = 10
  ): Promise<GroupCourseStats[]> {
    const response = await apiClient.get<GroupCourseStats[]>(
      `${STATS_BASE_URL}/${groupId}/courses?limit=${limit}`
    );
    return response.data;
  },

  /**
   * Pobiera feed aktywności grupy
   */
  async getActivityFeed(
    groupId: string,
    limit: number = 10
  ): Promise<GroupActivityItem[]> {
    const response = await apiClient.get<GroupActivityItem[]>(
      `${STATS_BASE_URL}/${groupId}/activity?limit=${limit}`
    );
    return response.data;
  },

  /**
   * Pobiera ranking grupy
   */
  async getLeaderboard(
    groupId: string,
    days: number = 30,
    limit: number = 10
  ): Promise<GroupLeaderboardEntry[]> {
    const response = await apiClient.get<GroupLeaderboardEntry[]>(
      `${STATS_BASE_URL}/${groupId}/leaderboard?days=${days}&limit=${limit}`
    );
    return response.data;
  },
};
