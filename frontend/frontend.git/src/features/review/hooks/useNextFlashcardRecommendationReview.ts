import {NextFlashcardRecommendation} from "@/features/learning/types/learning.types";
import {useQuery} from "@tanstack/react-query";
import {qk} from "@/lib/queryKeys";
import {getNextFlashcardReview} from "@/features/review/service/review.service";


export const useNextFlashcardRecommendationReview = (enrollmentId: string) => {
    return useQuery<NextFlashcardRecommendation>({
            queryKey: qk.learning.nextFlashcardReview(enrollmentId),
            queryFn: () => getNextFlashcardReview(enrollmentId),
            enabled: !!enrollmentId
        }

    )
}