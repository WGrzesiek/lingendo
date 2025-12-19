import {useInfiniteQuery, useQuery} from "@tanstack/react-query";
import {getReviewWordsView} from "@/features/review/service/review.service";
import {PageResponse} from "@/types/common";
import {IDeckListItem} from "@/features/deck/types";
import {getIDecks} from "@/features/deck/services/deck.service";
import {CourseContentItem, CourseWord} from "@/features/course/types/words.types";
import {getCourseWords} from "@/features/course/services/words.service";

export const useReviewWordsViewInfinite = (
    enrollmentId: string | null,
    pageSize = 10
) => {
    return useInfiniteQuery<PageResponse<CourseWord>, Error>({
        queryKey: ["reviewWords", enrollmentId, "infinite"],
        queryFn: async ({ pageParam = 0 }) => {
            return getReviewWordsView(enrollmentId!, {
                page: pageParam as number,
                size: pageSize,
            });
        },
        initialPageParam: 0,
        getNextPageParam: (lastPage) => {
            if (lastPage.last) return undefined;
            return lastPage.number + 1;
        },
        enabled: !!enrollmentId,
    });
};


