import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import {
  teacherStudentApi,
  studentTeacherApi,
  teacherDashboardStatsApi,
} from "../services/teacherStudentApi";
import type { CreateInvitationRequest } from "../types/api";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania zaproszeń nauczyciela
 */
export const useTeacherInvitations = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "invitations", page, size],
    queryFn: () => teacherStudentApi.getInvitations(page, size),
  });
};

/**
 * Hook do tworzenia zaproszenia
 */
export const useCreateInvitation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (request: CreateInvitationRequest) =>
      teacherStudentApi.createInvitation(request),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do dezaktywacji zaproszenia
 */
export const useDeactivateInvitation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (invitationId: string) =>
      teacherStudentApi.deactivateInvitation(invitationId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do usuwania zaproszenia
 */
export const useDeleteInvitation = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (invitationId: string) =>
      teacherStudentApi.deleteInvitation(invitationId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do pobierania informacji o zaproszeniu po kodzie
 */
export const useInvitationInfo = (code: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "invitationInfo", code],
    queryFn: () => teacherStudentApi.getInvitationInfo(code),
    enabled: !!code,
  });
};

// ==================== HOOKI DLA NAUCZYCIELA - UCZNIOWIE ====================

/**
 * Hook do pobierania listy uczniów
 */
export const useTeacherStudents = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "students", page, size],
    queryFn: () => teacherStudentApi.getStudents(page, size),
  });
};

/**
 * Hook do usuwania ucznia
 */
export const useRemoveStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (studentId: string) =>
      teacherStudentApi.removeStudent(studentId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do blokowania ucznia
 */
export const useBlockStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (studentId: string) =>
      teacherStudentApi.blockStudent(studentId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do odblokowywania ucznia
 */
export const useUnblockStudent = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (studentId: string) =>
      teacherStudentApi.unblockStudent(studentId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

// ==================== HOOKI DLA NAUCZYCIELA - STATYSTYKI ====================

/**
 * Hook do pobierania statystyk nauczyciela
 */
export const useTeacherStats = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "stats"],
    queryFn: () => teacherStudentApi.getTeacherStats(),
  });
};

/**
 * Hook do pobierania najlepszych uczniów
 */
export const useTopStudents = (limit: number = 5) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "topStudents", limit],
    queryFn: () => teacherDashboardStatsApi.getTopStudents(limit),
  });
};

/**
 * Hook do pobierania szczegółowych statystyk nauczyciela
 */
export const useTeacherStatsDetails = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "statsDetails"],
    queryFn: () => teacherDashboardStatsApi.getStatsDetails(),
  });
};

/**
 * Hook do pobierania aktywności uczniów
 */
export const useTeacherActivity = (limit: number = 10) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "activity", limit],
    queryFn: () => teacherDashboardStatsApi.getActivity(limit),
  });
};

// ==================== HOOKI DLA UCZNIA ====================

/**
 * Hook do dołączania do nauczyciela
 */
export const useJoinTeacher = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (invitationCode: string) =>
      studentTeacherApi.joinTeacher(invitationCode),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};

/**
 * Hook do pobierania listy nauczycieli ucznia
 */
export const useMyTeachers = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER_STUDENT, "myTeachers", page, size],
    queryFn: () => studentTeacherApi.getMyTeachers(page, size),
  });
};

/**
 * Hook do opuszczania nauczyciela
 */
export const useLeaveTeacher = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (teacherId: string) =>
      studentTeacherApi.leaveTeacher(teacherId),
    onSuccess: async () => {
      await queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.TEACHER_STUDENT],
      });
    },
  });
};
