import {useQuery} from "@tanstack/react-query";
import {getCourseProgress} from "@/features/course/services/course.service";

export const useCourseProgress = (enrollmentId: string) => {
    return useQuery(
        {
            queryKey: ['course-progress', enrollmentId],
            queryFn: () => getCourseProgress(enrollmentId),
            enabled: !!enrollmentId,
        }
    )
}