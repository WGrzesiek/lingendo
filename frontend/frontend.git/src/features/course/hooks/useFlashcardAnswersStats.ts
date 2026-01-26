import {useQuery} from "@tanstack/react-query";
import {getFlashcardAnswersStats} from "@/features/course/services/stats.service";

export const useFlashcardAnswersStats = (enrollmentId: string) => {
    return useQuery({
        queryKey: ['flashcardAnswersStats', enrollmentId],
        queryFn: () => getFlashcardAnswersStats(enrollmentId),
        enabled: !!enrollmentId,
    })
}