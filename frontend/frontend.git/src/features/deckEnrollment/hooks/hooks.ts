import { useMutation, useQueryClient } from "@tanstack/react-query";
import { QUERY_KEYS, REFETCH_GROUPS } from "@/lib/queryKeys";
import {
  enrollToDeck,
  updateFlashcardsPerSession,
  updateLearnAlgorithm,
  updateReviewSchedule,
} from "@/features/deckEnrollment/service/enrollment.service";
import type {
  CreateEnrollmentRequest,
  UpdateFlashcardsPerSessionRequest,
  UpdateLearnAlgorithmRequest,
  UpdateReviewScheduleRequest,
} from "@/features/deckEnrollment/type/enrollment";

/**
 * Hook do zapisywania się na talię (enrollment).
 */
export const useEnrollToDeck = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      deckId,
      data = {},
    }: {
      deckId: string;
      data?: CreateEnrollmentRequest;
    }) => enrollToDeck(deckId, data),
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_DECK_MUTATION.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};

/**
 * Hook do zmiany algorytmu nauki
 */
export const useUpdateLearnAlgorithm = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      enrollmentId,
      data,
    }: {
      enrollmentId: string;
      data: UpdateLearnAlgorithmRequest;
    }) => updateLearnAlgorithm(enrollmentId, data),
    onSuccess: async (enrollmentId) => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.ENROLLMENTS] });
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECKS] });
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES, enrollmentId, 'header'] });
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES, enrollmentId, 'progress'] });
    },
  });
};

/**
 * Hook do zmiany liczby fiszek na sesję (1-100)
 */
export const useUpdateFlashcardsPerSession = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      enrollmentId,
      data,
    }: {
      enrollmentId: string;
      data: UpdateFlashcardsPerSessionRequest;
    }) => updateFlashcardsPerSession(enrollmentId, data),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECKS] });
    },
  });
};

/**
 * Hook do zmiany harmonogramu powtórek
 */
export const useUpdateReviewSchedule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      enrollmentId,
      data,
    }: {
      enrollmentId: string;
      data: UpdateReviewScheduleRequest;
    }) => updateReviewSchedule(enrollmentId, data),
    onSuccess: async () => {
      await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.DECKS] });
    },
  });
};
