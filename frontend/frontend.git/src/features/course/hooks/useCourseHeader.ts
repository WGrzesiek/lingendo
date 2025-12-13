import {useQuery} from "@tanstack/react-query";
import {getCourseHeader} from "@/features/course/services/course.service";

export const useCourseHeader = (enrollmentId: string) => {
    return useQuery({
        queryKey: ['course-header', enrollmentId],
        queryFn: () => getCourseHeader(enrollmentId),
        enabled: !!enrollmentId,
    })
}