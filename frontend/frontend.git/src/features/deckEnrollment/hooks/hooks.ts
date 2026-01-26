import { useMutation, useQueryClient } from "@tanstack/react-query";
import { qk } from "@/lib/queryKeys";
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
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: qk.deckShare.sharedWithMe() });
      queryClient.invalidateQueries({ queryKey: qk.deck.all });
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
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail(variables.enrollmentId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deck.details(variables.enrollmentId),
      });
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
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail(variables.enrollmentId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deck.details(variables.enrollmentId),
      });
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
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({
        queryKey: qk.deck.detail(variables.enrollmentId),
      });
      queryClient.invalidateQueries({
        queryKey: qk.deck.details(variables.enrollmentId),
      });
    },
  });
};
