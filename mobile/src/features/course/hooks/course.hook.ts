import { useQuery, useMutation, useInfiniteQuery, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { courseService, wordsService, statsService } from '../services';
import type { PageResponse } from '@/types/common';
import type { CourseWord } from '../types';


export const useCourse = () => {
  const queryClient = useQueryClient();

  /**
   * Invaliduje wszystkie klucze z grupy
   */
  const invalidateGroup = (group: readonly string[]) => {
    group.forEach((key) => {
      queryClient.invalidateQueries({ queryKey: [key] });
    });
  };

  /**
   * Pobiera nagłówek kursu
   */
  const useCourseHeader = (enrollmentId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.COURSES, 'header', enrollmentId],
      queryFn: () => courseService.getCourseHeader(enrollmentId),
      enabled: !!enrollmentId,
    });

  /**
   * Pobiera postęp kursu
   */
  const useCourseProgress = (enrollmentId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.COURSES, 'progress', enrollmentId],
      queryFn: () => courseService.getCourseProgress(enrollmentId),
      enabled: !!enrollmentId,
    });

  /**
   * Pobiera ustawienia kursu
   */
  const useCourseSettings = (enrollmentId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.COURSES, 'settings', enrollmentId],
      queryFn: () => courseService.getCourseSettings(enrollmentId),
      enabled: !!enrollmentId,
    });

  /**
   * Pobiera słówka kursu z paginacją
   */
  const useCourseWords = (enrollmentId: string, page?: number, size?: number) =>
    useQuery({
      queryKey: [QUERY_KEYS.COURSES, 'words', enrollmentId, { page, size }],
      queryFn: () => wordsService.getCourseWords(enrollmentId, { page, size }),
      enabled: !!enrollmentId,
    });

  /**
   * Pobiera słówka kursu z nieskończonym scrollem
   */
  const useInfiniteCourseWords = (enrollmentId: string | null, pageSize = 10) =>
    useInfiniteQuery<PageResponse<CourseWord>, Error>({
      queryKey: [QUERY_KEYS.COURSES, 'words', enrollmentId, 'infinite'],
      queryFn: async ({ pageParam = 0 }) => {
        return wordsService.getCourseWords(enrollmentId!, {
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
   * Pobiera statystyki odpowiedzi dla fiszek
   */
  const useFlashcardAnswersStats = (enrollmentId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.COURSES, 'stats', enrollmentId],
      queryFn: () => statsService.getFlashcardAnswersStats(enrollmentId),
      enabled: !!enrollmentId,
    });

  // =====================
  // MUTATIONS - Modyfikacje danych
  // =====================

  /**
   * Inicjalizuje nową sesję nauki
   */
  const useInitializeSession = () =>
    useMutation({
      mutationFn: (enrollmentId: string) => courseService.initializeSession(enrollmentId),
      onSuccess: (_, enrollmentId) => {
        // queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.COURSES, 'progress', enrollmentId], });
        // queryClient.invalidateQueries({ queryKey: [QUERY_KEYS.COURSES, 'settings', enrollmentId], });
        // invalidateGroup(INVALIDATION_GROUPS.AFTER_LEARNING);
        // queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES] });
        queryClient.refetchQueries({ queryKey: [QUERY_KEYS.COURSES, 'progress', enrollmentId] });

      },
    });

  return {
    // Queries
    useCourseHeader,
    useCourseProgress,
    useCourseSettings,
    useCourseWords,
    useInfiniteCourseWords,
    useFlashcardAnswersStats,
    // Mutations
    useInitializeSession,
  };
};
