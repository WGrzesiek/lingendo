import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { studentGroupsApi } from "../services/studentGroupsApi";
import type {
  CreateGroupRequest,
  UpdateGroupRequest,
  AddMembersRequest,
  RemoveMembersRequest,
} from "../types/api";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania listy grup nauczyciela
 */
export const useTeacherGroups = (
  includeArchived: boolean = false,
  page: number = 0,
  size: number = 20
) => {
  return useQuery({
    queryKey: qk.studentGroups.groupsList(includeArchived, page, size),
    queryFn: () =>
      studentGroupsApi.getTeacherGroups(includeArchived, page, size),
  });
};

/**
 * Hook do pobierania szczegółów grupy
 */
export const useGroupDetail = (groupId: string) => {
  return useQuery({
    queryKey: qk.studentGroups.groupDetail(groupId),
    queryFn: () => studentGroupsApi.getGroup(groupId),
    enabled: !!groupId,
  });
};

/**
 * Hook do tworzenia grupy
 */
export const useCreateGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateGroupRequest) =>
      studentGroupsApi.createGroup(request),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groups(),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.stats(),
      });
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
      request,
    }: {
      groupId: string;
      request: UpdateGroupRequest;
    }) => studentGroupsApi.updateGroup(groupId, request),
    onSuccess: (_, { groupId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groups(),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
    },
  });
};

/**
 * Hook do archiwizacji grupy
 */
export const useArchiveGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (groupId: string) => studentGroupsApi.archiveGroup(groupId),
    onSuccess: (_, groupId) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groups(),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.stats(),
      });
    },
  });
};

/**
 * Hook do przywracania grupy
 */
export const useRestoreGroup = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (groupId: string) => studentGroupsApi.restoreGroup(groupId),
    onSuccess: (_, groupId) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groups(),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.stats(),
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
    mutationFn: (groupId: string) => studentGroupsApi.deleteGroup(groupId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groups(),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.stats(),
      });
    },
  });
};

// ==================== HOOKI DLA NAUCZYCIELA - STATYSTYKI ====================

/**
 * Hook do pobierania statystyk grup
 */
export const useGroupStats = () => {
  return useQuery({
    queryKey: qk.studentGroups.stats(),
    queryFn: () => studentGroupsApi.getGroupStats(),
  });
};

// ==================== HOOKI DLA NAUCZYCIELA - CZŁONKOWIE ====================

/**
 * Hook do pobierania członków grupy
 */
export const useGroupMembers = (
  groupId: string,
  page: number = 0,
  size: number = 20
) => {
  return useQuery({
    queryKey: qk.studentGroups.groupMembers(groupId, page, size),
    queryFn: () => studentGroupsApi.getGroupMembers(groupId, page, size),
    enabled: !!groupId,
  });
};

/**
 * Hook do dodawania członków do grupy
 */
export const useAddGroupMembers = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      groupId,
      request,
    }: {
      groupId: string;
      request: AddMembersRequest;
    }) => studentGroupsApi.addMembers(groupId, request),
    onSuccess: (_, { groupId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupMembers(groupId, 0, 20),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.stats(),
      });
    },
  });
};

/**
 * Hook do usuwania członków z grupy
 */
export const useRemoveGroupMembers = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      groupId,
      request,
    }: {
      groupId: string;
      request: RemoveMembersRequest;
    }) => studentGroupsApi.removeMembers(groupId, request),
    onSuccess: (_, { groupId }) => {
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupDetail(groupId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.groupMembers(groupId, 0, 20),
      });
      queryClient.invalidateQueries({
        queryKey: qk.studentGroups.stats(),
      });
    },
  });
};

// ==================== HOOKI DLA UCZNIA ====================

/**
 * Hook do pobierania grup, do których należy uczeń
 */
export const useStudentGroups = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: qk.studentGroups.myGroupsList(page, size),
    queryFn: () => studentGroupsApi.getStudentGroups(page, size),
  });
};
