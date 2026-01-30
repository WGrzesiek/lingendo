import {useQuery} from "@tanstack/react-query";
import {getCourseHeader} from "@/features/course/services/course.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

export const useCourseHeader = (enrollmentId: string) => {
    return useQuery({
        queryKey: [QUERY_KEYS.COURSES, enrollmentId, 'header'],
        queryFn: () => getCourseHeader(enrollmentId),
        enabled: !!enrollmentId,
    })
}