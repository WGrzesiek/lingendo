import {useQuery} from "@tanstack/react-query";
import {getReviewHeader} from "@/features/review/service/review.service";

export const useReviewHeader = (enrollmentId: string) => {
    return useQuery({
        queryKey: ['review-header', enrollmentId],
        queryFn: () => getReviewHeader(enrollmentId)
}
    )

}