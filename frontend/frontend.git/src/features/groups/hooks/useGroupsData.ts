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
import {qk} from "@/lib/queryKeys";

// ============================================
// HOOKI DLA ZARZĄDZANIA GRUPAMI
// ============================================

/**
 * Hook do pobierania szczegółów grupy
 */
export const useGroupDetail = (groupId: string) => {
  return useQuery({
    queryKey: qk.groups.detail(groupId),
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.groups.all });
      queryClient.invalidateQueries({ queryKey: qk.groups.withStats() });
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
    onSuccess: (_, { groupId }) => {
      queryClient.invalidateQueries({ queryKey: qk.groups.all });
      queryClient.invalidateQueries({queryKey: qk.studentGroups.groupDetail(groupId),
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.groups.all });
      queryClient.invalidateQueries({ queryKey: qk.groups.withStats() });
    },
  });
};

// ============================================
// HOOKI DLA CZŁONKÓW GRUPY
// ============================================

/**
 * Hook do pobierania członków grupy
 */
export const useGroupMembers = (
  groupId: string
) => {
  return useQuery({
    queryKey: qk.groups.members(groupId),
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
    onSuccess: (_, { groupId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.groups.members(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.groups.withStats(),
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
    onSuccess: (_, { groupId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.groups.members(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.groups.withStats(),
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
    queryKey: qk.groups.withStats(),
    queryFn: () => groupStatsService.getGroupsWithStats(),
  });
};

/**
 * Hook do pobierania pełnego dashboardu grupy
 */
export const useGroupDashboard = (groupId: string) => {
  return useQuery({
    queryKey: qk.groups.dashboard(groupId),
    queryFn: () => groupStatsService.getGroupDashboard(groupId),
    enabled: !!groupId,
  });
};

/**
 * Hook do pobierania kursów udostępnionych grupie
 */
export const useGroupCourses = (groupId: string, limit: number = 10) => {
  return useQuery({
    queryKey: qk.groups.courses(groupId, limit),
    queryFn: () => groupStatsService.getSharedCourses(groupId, limit),
    enabled: !!groupId,
  });
};

/**
 * Hook do pobierania aktywności grupy
 */
export const useGroupActivity = (groupId: string, limit: number = 10) => {
  return useQuery({
    queryKey: qk.groups.activity(groupId, limit),
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
  limit: number = 10
) => {
  return useQuery({
    queryKey: qk.groups.leaderboard(groupId, days, limit),
    queryFn: () => groupStatsService.getLeaderboard(groupId, days, limit),
    enabled: !!groupId,
  });
};
