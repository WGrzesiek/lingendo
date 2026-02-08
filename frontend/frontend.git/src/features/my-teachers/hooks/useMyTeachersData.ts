import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { myTeachersService } from "../services/myTeachersService";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania listy nauczycieli ucznia
 */
export const useMyTeachers = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: [QUERY_KEYS.MY_TEACHERS, "list", page],
    queryFn: () => myTeachersService.getMyTeachers(page, size),
  });
};

/**
 * Hook do pobierania informacji o zaproszeniu
 */
export const useInvitationInfo = (code: string) => {
  return useQuery({
    queryKey: [QUERY_KEYS.MY_TEACHERS, "invitationInfo", code],
    queryFn: () => myTeachersService.getInvitationInfo(code),
    enabled: code.length >= 6,
    retry: false,
  });
};

/**
 * Hook do dołączania do nauczyciela
 */
export const useJoinTeacher = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (invitationCode: string) =>
      myTeachersService.joinTeacher(invitationCode),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.MY_TEACHERS] });
    },
  });
};

/**
 * Hook do opuszczania nauczyciela
 */
export const useLeaveTeacher = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (teacherId: string) =>
      myTeachersService.leaveTeacher(teacherId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.MY_TEACHERS] });
    },
  });
};
