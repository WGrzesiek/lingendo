import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { teacherCourseService } from "../services/teacherService";
import { QUERY_KEYS } from "@/lib/queryKeys";

/**
 * Hook do pobierania kursów nauczyciela
 */
export const useTeacherCourses = () => {
  return useQuery({
    queryKey: [QUERY_KEYS.TEACHER, "courses"],
    queryFn: () => teacherCourseService.getCourses(),
  });
};

/**
 * Hook do udostępniania/cofania udostępnienia kursu
 */
export const useToggleCourseSharing = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ courseId, share }: { courseId: string; share: boolean }) =>
      share
        ? teacherCourseService.shareCourse(courseId)
        : teacherCourseService.unshareCourse(courseId),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.TEACHER] });
    },
  });
};
