import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { teacherCourseService } from "../services/teacherService";
import { qk } from "@/lib/queryKeys";

/**
 * Hook do pobierania kursów nauczyciela
 */
export const useTeacherCourses = () => {
  return useQuery({
    queryKey: qk.teacher.courses(),
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.teacher.courses() });
    },
  });
};
