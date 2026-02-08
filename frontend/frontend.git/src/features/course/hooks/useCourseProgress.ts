import {useQuery} from "@tanstack/react-query";
import {getCourseProgress} from "@/features/course/services/course.service";
import { QUERY_KEYS } from "@/lib/queryKeys";


export const useCourseProgress = (enrollmentId: string) => {
    return useQuery(
        {
            queryKey: [QUERY_KEYS.COURSES, enrollmentId, 'progress'],
            queryFn: () => getCourseProgress(enrollmentId),
            enabled: !!enrollmentId,
        }
    )
}