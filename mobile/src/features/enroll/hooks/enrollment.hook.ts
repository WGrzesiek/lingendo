import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { enrollmentService } from '../services';
import type { LearnAlgorithm, ReviewSchedule } from '@/features/deck/types';
import { CreateEnrollmentRequest } from '@/features/enroll';

export const useEnrollment = () => {
  const queryClient = useQueryClient();

  /**
   * Invaliduje wszystkie klucze z grupy
   */
  const invalidateGroup = (group: readonly string[]) => {
    group.forEach((key) => {
      queryClient.invalidateQueries({ queryKey: [key] });
    });
  };

  // =====================
  // MUTATIONS - Modyfikacje danych
  // =====================

  /**
   * Zapisuje użytkownika do talii
   */
  const useEnrollToDeck = () =>
    useMutation({
      mutationFn: ({ deckId, data = {} }: { deckId: string; data?: CreateEnrollmentRequest }) =>
        enrollmentService.enrollToDeck(deckId, data),
      onSuccess: () => {
        invalidateGroup(INVALIDATION_GROUPS.AFTER_ENROLLMENT);
      },
    });

  /**
   * Wypisuje użytkownika z talii
   */
  const useUnenrollFromDeck = () =>
    useMutation({
      mutationFn: (deckId: string) => enrollmentService.unenrollFromDeck(deckId),
      onSuccess: () => {
        invalidateGroup(INVALIDATION_GROUPS.AFTER_ENROLLMENT);
      },
    });

  /**
   * Aktualizuje algorytm nauki dla zapisu
   */
  const useUpdateLearnAlgorithm = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        algorithm,
      }: {
        enrollmentId: string;
        algorithm: LearnAlgorithm;
      }) => enrollmentService.updateLearnAlgorithm(enrollmentId, algorithm),
      onSuccess: async () => {
        await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES] });


      },
    });

  /**
   * Aktualizuje liczbę fiszek na sesję dla zapisu
   */
  const useUpdateFlashcardsPerSession = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        limit,
      }: {
        enrollmentId: string;
        limit: number;
      }) => enrollmentService.updateFlashcardsPerSession(enrollmentId, limit),
      onSuccess: async () => {
        await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES] });
      },
    });

  /**
   * Aktualizuje harmonogram powtórek dla zapisu
   */
  const useUpdateReviewSchedule = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        mode,
      }: {
        enrollmentId: string;
        mode: ReviewSchedule;
      }) => enrollmentService.updateReviewSchedule(enrollmentId, mode),
      onSuccess: async () => {
        await queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES] });
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
