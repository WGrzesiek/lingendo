import apiClient from "@/lib/api/axios";
import type {
  GroupResponse,
  CreateGroupRequest,
  UpdateGroupRequest,
  GroupMemberResponse,
  AddMembersRequest,
  RemoveMembersRequest,
  BatchMemberOperationResponse,
  GroupStatsResponse,
  PageResponse,
} from "../types/api";


const BASE_URL = "/v1/groups";

export const studentGroupsApi = {

  /**
   * Tworzy nową grupę
   */
  createGroup: async (request: CreateGroupRequest): Promise<GroupResponse> => {
    const response = await apiClient.post<GroupResponse>(BASE_URL, request);
    return response.data;
  },

  /**
   * Pobiera listę grup nauczyciela
   */
  getTeacherGroups: async (
    includeArchived: boolean = false,
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<GroupResponse>> => {
    const response = await apiClient.get<PageResponse<GroupResponse>>(
      BASE_URL,
      { params: { includeArchived, page, size } }
    );
    return response.data;
  },

  /**
   * Pobiera szczegóły grupy
   */
  getGroup: async (groupId: string): Promise<GroupResponse> => {
    const response = await apiClient.get<GroupResponse>(
      `${BASE_URL}/${groupId}`
    );
    return response.data;
  },

  /**
   * Aktualizuje grupę
   */
  updateGroup: async (
    groupId: string,
    request: UpdateGroupRequest
  ): Promise<GroupResponse> => {
    const response = await apiClient.patch<GroupResponse>(
      `${BASE_URL}/${groupId}`,
      request
    );
    return response.data;
  },

  /**
   * Archiwizuje grupę
   */
  archiveGroup: async (groupId: string): Promise<void> => {
    await apiClient.patch(`${BASE_URL}/${groupId}/archive`);
  },

  /**
   * Przywraca zarchiwizowaną grupę
   */
  restoreGroup: async (groupId: string): Promise<void> => {
    await apiClient.patch(`${BASE_URL}/${groupId}/restore`);
  },

  /**
   * Usuwa grupę
   */
  deleteGroup: async (groupId: string): Promise<void> => {
    await apiClient.delete(`${BASE_URL}/${groupId}`);
  },

  // ==================== STATYSTYKI ====================

  /**
   * Pobiera statystyki grup
   */
  getGroupStats: async (): Promise<GroupStatsResponse> => {
    const response = await apiClient.get<GroupStatsResponse>(
      `${BASE_URL}/stats`
    );
    return response.data;
  },

  // ==================== CZŁONKOWIE ====================

  /**
   * Dodaje uczniów do grupy
   */
  addMembers: async (
    groupId: string,
    request: AddMembersRequest
  ): Promise<BatchMemberOperationResponse> => {
    const response = await apiClient.post<BatchMemberOperationResponse>(
      `${BASE_URL}/${groupId}/members`,
      request
    );
    return response.data;
  },

  /**
   * Usuwa uczniów z grupy
   */
  removeMembers: async (
    groupId: string,
    request: RemoveMembersRequest
  ): Promise<BatchMemberOperationResponse> => {
    const response = await apiClient.delete<BatchMemberOperationResponse>(
      `${BASE_URL}/${groupId}/members`,
      { data: request }
    );
    return response.data;
  },

  /**
   * Pobiera członków grupy
   */
  getGroupMembers: async (
    groupId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<GroupMemberResponse>> => {
    const response = await apiClient.get<PageResponse<GroupMemberResponse>>(
      `${BASE_URL}/${groupId}/members`,
      { params: { page, size } }
    );
    return response.data;
  },

  // ==================== GRUPY UCZNIA ====================

  /**
   * Pobiera grupy, do których należy uczeń
   */
  getStudentGroups: async (
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<GroupResponse>> => {
    const response = await apiClient.get<PageResponse<GroupResponse>>(
      `${BASE_URL}/my`,
      { params: { page, size } }
    );
    return response.data;
  },
};
