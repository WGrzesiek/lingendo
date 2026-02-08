import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { QUERY_KEYS, INVALIDATION_GROUPS } from '@/constants';
import { learningService } from '../services';
import type { RememberAnswer, QuizAnswer, TypingAnswer } from '../types';

export const useLearning = () => {
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
   * Pobiera następną fiszkę do nauki
   */
  const useNextFlashcard = (sessionId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.LEARNING, 'next', sessionId],
      queryFn: () => learningService.getNextFlashcard(sessionId),
      enabled: !!sessionId,
    });

  /**
   * Pobiera postęp sesji nauki (dla nagłówka)
   */
  const useLearnHeaderProgress = (sessionId: string) =>
    useQuery({
      queryKey: [QUERY_KEYS.LEARNING, 'progress', sessionId],
      queryFn: () => learningService.getLearnHeaderProgress(sessionId),
      enabled: !!sessionId,
    });

  // =====================
  // MUTATIONS - Modyfikacje danych
  // =====================

  /**
   * Przesyła odpowiedź użytkownika na fiszkę
   */
  const useSubmitAnswer = () =>
    useMutation({
      mutationFn: ({
        sessionId,
        flashcardId,
        answer,
      }: {
        sessionId: string;
        flashcardId: string;
        answer: RememberAnswer | QuizAnswer | TypingAnswer;
      }) => learningService.submitAnswer(sessionId, flashcardId, answer),
      onSuccess: (_, variables) => {
        queryClient.invalidateQueries({
          queryKey: [QUERY_KEYS.LEARNING, 'next', variables.sessionId],
        });
        queryClient.invalidateQueries({
          queryKey: [QUERY_KEYS.LEARNING, 'progress', variables.sessionId],
        });
      },
    });

  /**
   * Kończy sesję nauki
   */
  const useCompleteSession = () =>
    useMutation({
      mutationFn: (sessionId: string) => learningService.completeSession(sessionId),
      onSuccess: () => {
        invalidateGroup(INVALIDATION_GROUPS.AFTER_LEARNING);
      },
    });

  return {
    // Queries
    useNextFlashcard,
    useLearnHeaderProgress,
    // Mutations
    useSubmitAnswer,
    useCompleteSession,
  };
};
