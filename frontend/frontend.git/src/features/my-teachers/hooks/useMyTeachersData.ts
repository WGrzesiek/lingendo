import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { myTeachersService } from "../services/myTeachersService";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania listy nauczycieli ucznia
 */
export const useMyTeachers = (page: number = 0, size: number = 20) => {
  return useQuery({
    queryKey: qk.myTeachers.list(page),
    queryFn: () => myTeachersService.getMyTeachers(page, size),
  });
};

/**
 * Hook do pobierania informacji o zaproszeniu
 */
export const useInvitationInfo = (code: string) => {
  return useQuery({
    queryKey: qk.myTeachers.invitationInfo(code),
    queryFn: () => myTeachersService.getInvitationInfo(code),
    enabled: code.length >= 6, // Włącz tylko gdy kod ma sensowną długość
    retry: false, // Nie ponawiaj przy błędzie (kod może być nieprawidłowy)
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
    onSuccess: () => {
      // Odśwież listę nauczycieli po dołączeniu
      queryClient.invalidateQueries({ queryKey: qk.myTeachers.all });
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.myTeachers.all });
    },
  });
};
