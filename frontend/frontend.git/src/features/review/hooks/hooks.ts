import {
  FlashcardInteractionResult,
  NextFlashcardRecommendation,
  TypingAnswer,
} from "@/features/learning/types/learning.types";
import {
  useQuery,
  useInfiniteQuery,
  useMutation,
  useQueryClient,
} from "@tanstack/react-query";
import { qk } from "@/lib/queryKeys";
import {
  getNextFlashcardReview,
  getReviewHeader,
  getReviewWordsView,
  submitAnswerReview,
} from "@/features/review/service/review.service";
import { PageResponse } from "@/types/common";
import { CourseWord } from "@/features/course/types/words.types";

export const useNextFlashcardRecommendationReview = (enrollmentId: string) => {
  return useQuery<NextFlashcardRecommendation>({
    queryKey: qk.learning.nextFlashcardReview(enrollmentId),
    queryFn: () => getNextFlashcardReview(enrollmentId),
    enabled: !!enrollmentId,
  });
};

export const useReviewHeader = (enrollmentId: string) => {
  return useQuery({
    queryKey: qk.review.header(enrollmentId),
    queryFn: () => getReviewHeader(enrollmentId),
  });
};

export const useReviewWordsViewInfinite = (
  enrollmentId: string | null,
  pageSize = 10
) => {
  return useInfiniteQuery<PageResponse<CourseWord>, Error>({
    queryKey: qk.review.words(enrollmentId || ""),
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

export const useSubmitAnswerMutationReview = () => {
  const queryClient = useQueryClient();
  return useMutation<
    FlashcardInteractionResult,
    Error,
    { enrollmentId: string; flashcardId: string; answer: TypingAnswer }
  >({
    mutationFn: ({ flashcardId, answer }) =>
      submitAnswerReview(flashcardId, answer),
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.learning.nextFlashcardReview(variables.enrollmentId),
      });
    },
  });
};
