import {useQuery} from "@tanstack/react-query";
import {getCourseSettings} from "@/features/course/services/course.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

export const useCourseSettings = (enrollmentId: string) => {
    return useQuery(
        {
            queryKey: [QUERY_KEYS.COURSES, enrollmentId, 'settings'],
            queryFn: () => getCourseSettings(enrollmentId),
            enabled: !!enrollmentId,
        }
    )

}