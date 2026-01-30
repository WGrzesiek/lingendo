import {useQuery} from "@tanstack/react-query";
import {getFlashcardAnswersStats} from "@/features/course/services/stats.service";
import { QUERY_KEYS } from "@/lib/queryKeys";

export const useFlashcardAnswersStats = (enrollmentId: string) => {
    return useQuery({
        queryKey: [QUERY_KEYS.COURSES, enrollmentId, 'flashcardAnswersStats'],
        queryFn: () => getFlashcardAnswersStats(enrollmentId),
        enabled: !!enrollmentId,
    })
}