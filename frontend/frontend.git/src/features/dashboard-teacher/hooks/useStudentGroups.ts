import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { studentGroupsApi } from "../services/studentGroupsApi";
import type {
  CreateGroupRequest,
  UpdateGroupRequest,
  AddMembersRequest,
  RemoveMembersRequest,
} from "../types/api";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania listy grup nauczyciela
 */
export const useTeacherGroups = (
  includeArchived: boolean = false,
  page: number = 0,
  size: number = 20,
) => {
  return useQuery({
    queryKey: [
      QUERY_KEYS.STUDENT_GROUPS,
      "groups",
      includeArchived,
      page,
      size,
    ],
    queryFn: () =>
      studentGroupsApi.getTeacherGroups(includeArchived, page, size),
  });
};

/**
 * Hook do pobierania szczegółów grupy
 */
export const useGroupDetail = (groupId: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.STUDENT_GROUPS, "groupDetail", groupId],
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
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
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
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
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
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
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
    onSuccess: async () => {
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
    mutationFn: (groupId: string) => studentGroupsApi.deleteGroup(groupId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
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
    queryKey: [QUERY_KEYS.STUDENT_GROUPS, "stats"],
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
  size: number = 20,
) => {
  return useQuery({
    queryKey: [QUERY_KEYS.STUDENT_GROUPS, "members", groupId, page, size],
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
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
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
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.STUDENT_GROUPS],
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
    queryKey: [QUERY_KEYS.STUDENT_GROUPS, "myGroups", page, size],
    queryFn: () => studentGroupsApi.getStudentGroups(page, size),
  });
};
