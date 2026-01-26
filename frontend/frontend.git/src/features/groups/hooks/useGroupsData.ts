import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  groupsService,
  groupMembersService,
  groupStatsService,
} from "../services/groupsService";
import type {
  CreateGroupDto,
  UpdateGroupDto,
  AddGroupMembersBatchDto,
} from "../types/group.types";
import { QUERY_KEYS } from "@/lib/queryKeys";

// ============================================
// HOOKI DLA ZARZĄDZANIA GRUPAMI
// ============================================

/**
 * Hook do pobierania szczegółów grupy
 */
export const useGroupDetail = (groupId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "detail", groupId],
    queryFn: () => groupsService.getGroupById(groupId),
    enabled: !!groupId,
  });
};

/**
 * Hook do tworzenia grupy
 */
export const useCreateGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: CreateGroupDto) => groupsService.createGroup(data),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.GROUPS] });
    },
  });
};

/**
 * Hook do aktualizacji grupy
 */
export const useUpdateGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      groupId,
      data,
    }: {
      groupId: string;
      data: UpdateGroupDto;
    }) => groupsService.updateGroup(groupId, data),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.GROUPS] });
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
      });
    },
  });
};

/**
 * Hook do usuwania grupy
 */
export const useDeleteGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (groupId: string) => groupsService.deleteGroup(groupId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.GROUPS] });
    },
  });
};

// ============================================
// HOOKI DLA CZŁONKÓW GRUPY
// ============================================

/**
 * Hook do pobierania członków grupy
 */
export const useGroupMembers = (groupId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "members", groupId],
    queryFn: () => groupMembersService.getMembers(groupId),
    enabled: !!groupId,
  });
};

/**
 * Hook do dodawania członków do grupy
 */
export const useAddGroupMembersBatch = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      groupId,
      data,
    }: {
      groupId: string;
      data: AddGroupMembersBatchDto;
    }) => groupMembersService.addMembersBatch(groupId, data),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.GROUPS] });
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
      });
    },
  });
};

/**
 * Hook do usuwania członków z grupy (jeden lub wielu)
 * jest jedna metoda odrazu bath
 */
export const useRemoveGroupMembersBatch = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      groupId,
      studentIds,
    }: {
      groupId: string;
      studentIds: string[];
    }) => groupMembersService.removeMembersBatch(groupId, studentIds),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.GROUPS] });
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
      });
    },
  });
};

// ============================================
// HOOKI DLA STATYSTYK GRUP
// ============================================

/**
 * Hook do pobierania listy grup ze statystykami
 */
export const useGroupsWithStats = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "withStats"],
    queryFn: () => groupStatsService.getGroupsWithStats(),
  });
};

/**
 * Hook do pobierania pełnego dashboardu grupy
 */
export const useGroupDashboard = (groupId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "dashboard", groupId],
    queryFn: () => groupStatsService.getGroupDashboard(groupId),
    enabled: !!groupId,
  });
};

/**
 * Hook do pobierania kursów udostępnionych grupie
 */
export const useGroupCourses = (groupId: string, limit: number = 10) => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "courses", groupId, limit],
    queryFn: () => groupStatsService.getSharedCourses(groupId, limit),
    enabled: !!groupId,
  });
};

/**
 * Hook do pobierania aktywności grupy
 */
export const useGroupActivity = (groupId: string, limit: number = 10) => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "activity", groupId, limit],
    queryFn: () => groupStatsService.getActivityFeed(groupId, limit),
    enabled: !!groupId,
  });
};

/**
 * Hook do pobierania rankingu grupy
 */
export const useGroupLeaderboard = (
  groupId: string,
  days: number = 30,
  limit: number = 10,
) => {
  return useQuery({
    queryKey: [QUERY_KEYS.GROUPS, "leaderboard", groupId, days, limit],
    queryFn: () => groupStatsService.getLeaderboard(groupId, days, limit),
    enabled: !!groupId,
  });
};
