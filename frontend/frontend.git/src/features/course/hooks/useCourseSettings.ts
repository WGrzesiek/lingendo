import {useQuery} from "@tanstack/react-query";
import {getCourseSettings} from "@/features/course/services/course.service";

export const useCourseSettings = (enrollmentId: string) => {
    return useQuery(
        {
            queryKey: ['course-settings', enrollmentId],
            queryFn: () => getCourseSettings(enrollmentId),
            enabled: !!enrollmentId,
        }
    )

}