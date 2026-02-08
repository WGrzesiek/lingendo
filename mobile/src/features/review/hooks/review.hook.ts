import { useQuery, useInfiniteQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS } from '@/constants';
import { reviewService } from '../services';
import type { PageResponse } from '@/types/common';
import type { CourseWord } from '@/features/course/types';
import type { TypingAnswer } from '@/features/learning';

export const useReview = () => {
  const queryClient = useQueryClient();

  /**
   * Pobiera nagłówek powtórek (statystyki)
   */
  const useReviewHeader = (enrollmentId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.REVIEW, 'header', enrollmentId],
      queryFn: () => reviewService.getReviewHeader(enrollmentId),
      enabled: !!enrollmentId,
    });

  /**
   * Pobiera listę słówek do powtórki (infinite scroll)
   */
  const useReviewWordsInfinite = (enrollmentId: string, pageSize = 10) =>
    useInfiniteQuery<PageResponse<CourseWord>, Error>({
      queryKey: [QUERY_KEYS.REVIEW, 'words', enrollmentId],
      queryFn: async ({ pageParam = 0 }) => {
        return reviewService.getReviewWords(enrollmentId, {
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

  /**
   * Pobiera następną fiszkę do powtórki
   */
  const useNextFlashcardReview = (enrollmentId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.REVIEW, 'next', enrollmentId],
      queryFn: () => reviewService.getNextFlashcard(enrollmentId),
      enabled: !!enrollmentId,
    });

  /**
   * Przesyła odpowiedź na pytanie powtórkowe
   */
  const useSubmitAnswerReview = () =>
    useMutation({
      mutationFn: ({
        flashcardId,
        answer,
      }: {
        enrollmentId: string;
        flashcardId: string;
        answer: TypingAnswer;
      }) => reviewService.submitAnswer(flashcardId, answer),
      onSuccess: (_, variables) => {
        queryClient.invalidateQueries({
          queryKey: [QUERY_KEYS.REVIEW, 'next', variables.enrollmentId],
        });
        queryClient.invalidateQueries({
          queryKey: [QUERY_KEYS.REVIEW, 'header', variables.enrollmentId],
        });
      },
    });

  return {
    useReviewHeader,
    useReviewWordsInfinite,
    useNextFlashcardReview,
    useSubmitAnswerReview,
  };
};
