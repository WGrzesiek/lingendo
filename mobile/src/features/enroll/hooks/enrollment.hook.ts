import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { enrollmentService } from '../services/enrollment.service';
import type {
  CreateEnrollmentRequest,
  UpdateFlashcardsPerSessionRequest,
  UpdateLearnAlgorithmRequest,
  UpdateReviewScheduleRequest,
} from '../types';

export const useEnrollment = () => {
  const queryClient = useQueryClient();

  // =====================
  // MUTATIONS - Modyfikacje danych
  // =====================

  const useEnrollToDeck = () =>
    useMutation({
      mutationFn: ({ deckId, data }: { deckId: string; data?: CreateEnrollmentRequest }) =>
        enrollmentService.enrollToDeck(deckId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: INVALIDATION_GROUPS.AFTER_DECK_MUTATION });
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DASHBOARD });
      },
    });

  const useUnenrollFromDeck = () =>
    useMutation({
      mutationFn: (enrollmentId: string) => enrollmentService.unenrollFromDeck(enrollmentId),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: INVALIDATION_GROUPS.AFTER_DECK_MUTATION });
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DASHBOARD });
      },
    });

  const useUpdateLearnAlgorithm = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        data,
      }: {
        enrollmentId: string;
        data: UpdateLearnAlgorithmRequest;
      }) => enrollmentService.updateLearnAlgorithm(enrollmentId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateFlashcardsPerSession = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        data,
      }: {
        enrollmentId: string;
        data: UpdateFlashcardsPerSessionRequest;
      }) => enrollmentService.updateFlashcardsPerSession(enrollmentId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  const useUpdateReviewSchedule = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        data,
      }: {
        enrollmentId: string;
        data: UpdateReviewScheduleRequest;
      }) => enrollmentService.updateReviewSchedule(enrollmentId, data),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: QUERY_KEYS.DECKS });
      },
    });

  return {
    useEnrollToDeck,
    useUnenrollFromDeck,
    useUpdateLearnAlgorithm,
    useUpdateFlashcardsPerSession,
    useUpdateReviewSchedule,
  };
};
