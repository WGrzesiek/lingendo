import { useMutation, useQueryClient, useQuery } from "@tanstack/react-query";
import { QUERY_KEYS, REFETCH_GROUPS } from "@/lib/queryKeys";
import {
  completeSession,
  getLearnHeaderProgress,
  getNextFlashcard,
  submitAnswer,
} from "@/features/learning/service/learning.service";
import {
  LearnHeaderProgress,
  NextFlashcardRecommendation,
  QuizAnswer,
  RememberAnswer,
  TypingAnswer,
} from "@/features/learning/types/learning.types";

export const useCompleteSession = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (sessionId: string) => completeSession(sessionId),
    onSuccess: async () => {
      await Promise.all(
        REFETCH_GROUPS.AFTER_LEARNING.map((key) =>
          queryClient.refetchQueries({ queryKey: [key] }),
        ),
      );
    },
  });
};

export const useLearnHeaderProgress = (sessionId: string) => {
  return useQuery<LearnHeaderProgress>({
    queryKey: [QUERY_KEYS.LEARNING, "headerProgress", sessionId],
    queryFn: () => getLearnHeaderProgress(sessionId),
    enabled: !!sessionId,
  });
};

export const useNextFlashcardRecommendation = (sessionId: string) => {
  return useQuery<NextFlashcardRecommendation>({
    queryKey: [QUERY_KEYS.LEARNING, "session", sessionId],
    queryFn: () => getNextFlashcard(sessionId),
    enabled: !!sessionId,
  });
};

export const useSubmitAnswerMutation = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      sessionId,
      flashcardId,
      answer,
    }: {
      sessionId: string;
      flashcardId: string;
      answer: RememberAnswer | QuizAnswer | TypingAnswer;
    }) => submitAnswer(sessionId, flashcardId, answer),
    onSuccess: (_, variables) => {
      queryClient.refetchQueries({
        queryKey: [QUERY_KEYS.LEARNING, "session", variables.sessionId],
      });
    },
  });
};
