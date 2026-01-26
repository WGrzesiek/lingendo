import { useMutation, useQueryClient } from "@tanstack/react-query";
import { initializeSession } from "@/features/course/services/course.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

export const useInitializeSession = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (enrollmentId: string) => initializeSession(enrollmentId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES] });
    },
  });
};
