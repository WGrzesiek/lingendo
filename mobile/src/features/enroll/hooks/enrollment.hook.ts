import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { enrollmentService } from '../services';
import type { LearnAlgorithm, ReviewSchedule } from '@/features/deck/types';

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
      mutationFn: (deckId: string) => enrollmentService.enrollToDeck(deckId),
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
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.ENROLLMENTS] });
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Aktualizuje liczbę fiszek na sesję dla zapisu
   */
  const useUpdateFlashcardsPerSession = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        flashcardsPerSession,
      }: {
        enrollmentId: string;
        flashcardsPerSession: number;
      }) => enrollmentService.updateFlashcardsPerSession(enrollmentId, flashcardsPerSession),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.ENROLLMENTS] });
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
      },
    });

  /**
   * Aktualizuje harmonogram powtórek dla zapisu
   */
  const useUpdateReviewSchedule = () =>
    useMutation({
      mutationFn: ({
        enrollmentId,
        reviewSchedule,
      }: {
        enrollmentId: string;
        reviewSchedule: ReviewSchedule;
      }) => enrollmentService.updateReviewSchedule(enrollmentId, reviewSchedule),
      onSuccess: () => {
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.ENROLLMENTS] });
        queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.DECKS] });
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
